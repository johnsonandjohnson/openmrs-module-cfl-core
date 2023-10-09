/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.service.impl;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.htmlformentry.model.Interruption;
import org.openmrs.module.cflcore.api.htmlformentry.model.Treatment;
import org.openmrs.module.cflcore.api.htmlformentry.service.TreatmentService;
import org.openmrs.module.cflcore.api.service.CFLEncounterService;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.module.cflcore.db.ExtendedOrderDAO;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class TreatmentServiceImpl implements TreatmentService {
  private static final String ENCOUNTER_VOIDING_REASON = "Replaced by a new encounter";
  private static final String HIV_PROGRAM_UUID_GP_KEY = "cfl.hivProgramUuid";
  private static final String HIV_PROGRAM_UUID_DEFAULT_VALUE =
      "06c596e7-53dd-44c1-9609-f1406fd9e76d";
  private static final String PROGRAM_FORM_ENCOUNTER_TYPE_UUID =
      "b3af129b-90c9-4f84-966e-f4f9515083e6";

  @Override
  public List<Treatment> getAllTreatments(Patient patient) {
    return findLatestProgramFormEncounter(patient)
        .map(this::buildAllTreatments)
        .orElse(Collections.emptyList());
  }

  @Override
  public Optional<Treatment> getCurrentTreatment(Patient patient, Date encounterDate) {
    final List<Treatment> allTreatments = getAllTreatments(patient);

    for (Treatment treatment : allTreatments) {
      if (!encounterDate.before(treatment.getStartDate())
          && (treatment.getStopDate() == null || encounterDate.before(treatment.getStopDate()))) {
        return Optional.of(treatment);
      }
    }

    return Optional.empty();
  }

  @Override
  public void voidPreviousProgramEncounters(Encounter currentEncounter) {
    List<Encounter> encounters =
        Context.getService(CFLEncounterService.class)
            .getEncountersByPatientAndType(
                currentEncounter.getPatient(), currentEncounter.getEncounterType());
    Optional<Encounter> encounterToVoid = findEncounterToVoid(encounters);
    encounterToVoid.ifPresent(
        value -> Context.getEncounterService().voidEncounter(value, ENCOUNTER_VOIDING_REASON));
  }

  @Override
  public void updatePatientProgram(Encounter currentEncounter) {
    if (isEncounterWithCurrentRegimen(currentEncounter)) {
      Optional<PatientProgram> hivPatientProgram =
          findLatestHIVPatientProgram(currentEncounter.getPatient());
      hivPatientProgram.ifPresent(program -> updatePatientProgram(program, currentEncounter));
    }
  }

  @Override
  public void stopOngoingDrugOrders(Encounter currentEncounter) {
    final TreatmentService treatmentService = Context.getService(TreatmentService.class);

    final Optional<Treatment> currentTreatment =
        treatmentService.getCurrentTreatment(
            currentEncounter.getPatient(), currentEncounter.getEncounterDatetime());

    if (currentTreatment.isPresent()) {
      final ExtendedOrderDAO extendedOrderDAO = getExtendedOrderDAO();

      final List<Order> ordersToStop =
          extendedOrderDAO.getActiveOrdersWithOtherNonCodedReason(
              currentEncounter.getPatient(), currentTreatment.get().getRegimenName());

      ordersToStop.forEach(
          order -> extendedOrderDAO.stopOrder(order, currentTreatment.get().getStartDate()));
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
    return programService.getPatientPrograms(patient, hivProgram, null, null, null, null, false)
        .stream()
        .max(Comparator.comparing(PatientProgram::getPatientProgramId));
  }

  private void updatePatientProgram(PatientProgram patientProgram, Encounter currentEncounter) {
    patientProgram.setDateEnrolled(
        DateUtil.getDateWithTimeOfDay(
            currentEncounter.getEncounterDatetime(), "00:00", DateUtil.getDefaultSystemTimeZone()));
    patientProgram.setDateCompleted(null);
    patientProgram.setDateChanged(DateUtil.now());
    Context.getService(ProgramWorkflowService.class).savePatientProgram(patientProgram);
  }

  private ExtendedOrderDAO getExtendedOrderDAO() {
    final List<ExtendedOrderDAO> implementations =
        Context.getRegisteredComponents(ExtendedOrderDAO.class);

    if (implementations.isEmpty()) {
      throw new IllegalStateException("Missing an ExtendedOrderDAO Spring bean.");
    }

    return implementations.get(0);
  }

  private Optional<Encounter> findLatestProgramFormEncounter(Patient patient) {
    final EncounterService encounterService = Context.getEncounterService();
    final EncounterType encounterType =
        encounterService.getEncounterTypeByUuid(PROGRAM_FORM_ENCOUNTER_TYPE_UUID);
    final EncounterSearchCriteria searchCriteria =
        new EncounterSearchCriteriaBuilder()
            .setPatient(patient)
            .setEncounterTypes(Collections.singletonList(encounterType))
            .setIncludeVoided(false)
            .createEncounterSearchCriteria();
    return encounterService.getEncounters(searchCriteria).stream()
        .max(Comparator.comparing(Encounter::getEncounterId));
  }

  private List<Treatment> buildAllTreatments(Encounter programFormEncounter) {
    final List<Obs> treatmentGroups = findTreatmentGroups(programFormEncounter);
    return treatmentGroups.stream()
        .map(this::createTreatment)
        .sorted(Comparator.comparing(Treatment::getStartDate))
        .collect(Collectors.toList());
  }

  private List<Obs> findTreatmentGroups(Encounter currentEncounter) {
    List<Obs> obsList = new ArrayList<>(currentEncounter.getAllObs());
    return obsList.stream()
        .filter(
            obs ->
                obs.getConcept()
                    .equals(
                        Context.getConceptService()
                            .getConceptByUuid(Treatment.TREATMENT_DETAILS_GROUP_CONCEPT_UUID)))
        .collect(Collectors.toList());
  }

  private Treatment createTreatment(Obs treatmentGroup) {
    final Treatment treatment = new Treatment();

    final List<Interruption> interruptions = new ArrayList<>();
    for (Obs obs : treatmentGroup.getGroupMembers()) {
      buildTreatment(treatment, obs);
      buildInterruptions(interruptions, obs);
    }
    treatment.setInterruptions(interruptions);

    return treatment;
  }

  private void buildTreatment(Treatment treatment, Obs treatmentChild) {
    ConceptService conceptService = Context.getConceptService();

    if (treatmentChild
        .getConcept()
        .equals(conceptService.getConceptByUuid(Treatment.TREATMENT_START_DATE_CONCEPT_UUID))) {
      treatment.setStartDate(treatmentChild.getValueDatetime());
    }

    if (treatmentChild
        .getConcept()
        .equals(conceptService.getConceptByUuid(Treatment.TREATMENT_STOP_DATE_CONCEPT_UUID))) {
      treatment.setStopDate(treatmentChild.getValueDatetime());
    }

    if (treatmentChild
        .getConcept()
        .equals(conceptService.getConceptByUuid(Treatment.TREATMENT_REGIMEN_CONCEPT_UUID))) {
      treatment.setRegimenName(treatmentChild.getValueText());
    }

    if (treatmentChild
        .getConcept()
        .equals(conceptService.getConceptByUuid(Treatment.TREATMENT_REASON_CONCEPT_UUID))) {
      treatment.setReason(treatmentChild.getValueCoded().getName().getName());
    }

    if (treatmentChild
        .getConcept()
        .equals(conceptService.getConceptByUuid(Treatment.CURRENT_REGIMEN_CONCEPT_UUID))) {
      treatment.setCurrent(treatmentChild.getValueBoolean());
    }
  }

  private void buildInterruptions(List<Interruption> interruptions, Obs interruptionParent) {
    if (interruptionParent
        .getConcept()
        .equals(
            Context.getConceptService()
                .getConceptByUuid(Interruption.INTERRUPTION_DETAILS_GROUP_CONCEPT_UUID))) {
      ConceptService conceptService = Context.getConceptService();

      Interruption interruption = new Interruption();
      for (Obs interruptionChild : interruptionParent.getGroupMembers()) {
        if (interruptionChild
            .getConcept()
            .equals(
                conceptService.getConceptByUuid(
                    Interruption.INTERRUPTION_START_DATE_CONCEPT_UUID))) {
          interruption.setStartDate(interruptionChild.getValueDatetime());
        }

        if (interruptionChild
            .getConcept()
            .equals(
                conceptService.getConceptByUuid(Interruption.INTERRUPTION_REASON_CONCEPT_UUID))) {
          interruption.setReason(interruptionChild.getValueText());
        }

        if (interruptionChild
            .getConcept()
            .equals(
                conceptService.getConceptByUuid(
                    Interruption.INTERRUPTION_RESTART_DATE_CONCEPT_UUID))) {
          interruption.setRestartDate(interruptionChild.getValueDatetime());
        }
      }

      interruptions.add(interruption);
    }
  }
}
