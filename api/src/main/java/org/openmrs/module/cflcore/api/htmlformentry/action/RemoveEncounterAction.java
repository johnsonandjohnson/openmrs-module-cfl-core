/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.action;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.htmlformentry.model.Treatment;
import org.openmrs.module.cflcore.api.service.CFLEncounterService;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RemoveEncounterAction implements CustomFormSubmissionAction {

  private static final String ENCOUNTER_VOIDING_REASON = "Replaced by a new encounter";

  private static final String HIV_PROGRAM_UUID_GP_KEY = "cfl.hivProgramUuid";

  private static final String HIV_PROGRAM_UUID_DEFAULT_VALUE =
      "06c596e7-53dd-44c1-9609-f1406fd9e76d";

  @Override
  public void applyAction(FormEntrySession formEntrySession) {
    Encounter currentEncounter = formEntrySession.getEncounter();
    List<Encounter> encounters =
        Context.getService(CFLEncounterService.class)
            .getEncountersByPatientAndType(
                currentEncounter.getPatient(), currentEncounter.getEncounterType());
    Optional<Encounter> encounterToVoid = findEncounterToVoid(encounters);
    encounterToVoid.ifPresent(
        value -> Context.getEncounterService().voidEncounter(value, ENCOUNTER_VOIDING_REASON));

    if (isEncounterWithCurrentRegimen(currentEncounter)) {
      Optional<PatientProgram> hivPatientProgram =
          findLatestHIVPatientProgram(formEntrySession.getPatient());
      hivPatientProgram.ifPresent(this::updatePatientProgram);
    }
  }

  private Optional<Encounter> findEncounterToVoid(List<Encounter> encounters) {
    if (encounters.size() > 1) {
      return encounters.stream().min(Comparator.comparing(Encounter::getEncounterId));
    }
    return Optional.empty();
  }

  private boolean isEncounterWithCurrentRegimen(Encounter encounter) {
    return encounter.getAllObs().stream()
        .anyMatch(
            obs ->
                obs.getConcept()
                    .equals(
                        Context.getConceptService()
                            .getConceptByUuid(Treatment.CURRENT_REGIMEN_CONCEPT_UUID)));
  }

  private Optional<PatientProgram> findLatestHIVPatientProgram(Patient patient) {
    ProgramWorkflowService programService = Context.getProgramWorkflowService();
    Program hivProgram =
        programService.getProgramByUuid(
            Context.getAdministrationService()
                .getGlobalProperty(HIV_PROGRAM_UUID_GP_KEY, HIV_PROGRAM_UUID_DEFAULT_VALUE));
    return programService
        .getPatientPrograms(patient, hivProgram, null, null, null, null, false)
        .stream()
        .max(Comparator.comparing(PatientProgram::getPatientProgramId));
  }

  private void updatePatientProgram(PatientProgram patientProgram) {
    patientProgram.setDateEnrolled(
        DateUtil.getDateWithTimeOfDay(
            DateUtil.now(), "00:00", DateUtil.getDefaultSystemTimeZone()));
    patientProgram.setDateCompleted(null);
    patientProgram.setDateChanged(DateUtil.now());
    Context.getService(ProgramWorkflowService.class).savePatientProgram(patientProgram);
  }
}
