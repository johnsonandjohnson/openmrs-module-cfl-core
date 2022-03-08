package org.openmrs.module.cfl.api.htmlformentry.action;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.service.CFLEncounterService;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DiscontinueHIVProgramAction implements CustomFormSubmissionAction {

  private static final String HIV_PROGRAM_ENCOUNTER_TYPE_UUID_GP_KEY =
      "cfl.hivProgramEncounterTypeUuid";

  private static final String HIV_PROGRAM_ENCOUNTER_TYPE_UUID_DEFAULT_VALUE =
      "b3af129b-90c9-4f84-966e-f4f9515083e6";

  private static final String HIV_PROGRAM_UUID_GP_KEY = "cfl.hivProgramUuid";

  private static final String HIV_PROGRAM_UUID_DEFAULT_VALUE =
      "06c596e7-53dd-44c1-9609-f1406fd9e76d";

  private static final String VOID_PATIENT_PROGRAM_REASON =
      "Related patient program has been completed";

  @Override
  public void applyAction(FormEntrySession formEntrySession) {
    Patient patient = formEntrySession.getPatient();

    Optional<Encounter> latestHIVProgramEncounter = getLatestHIVProgramEncounter(patient);
    latestHIVProgramEncounter.ifPresent(
        value -> Context.getEncounterService().voidEncounter(value, VOID_PATIENT_PROGRAM_REASON));

    Optional<PatientProgram> patientProgram = findLatestHIVPatientProgram(patient);
    patientProgram.ifPresent(
        program ->
            Context.getProgramWorkflowService()
                .voidPatientProgram(program, VOID_PATIENT_PROGRAM_REASON));
  }

  private Optional<Encounter> getLatestHIVProgramEncounter(Patient patient) {
    EncounterType hivProgramEncounterType =
        Context.getEncounterService()
            .getEncounterTypeByUuid(
                Context.getAdministrationService()
                    .getGlobalProperty(
                        HIV_PROGRAM_ENCOUNTER_TYPE_UUID_GP_KEY,
                        HIV_PROGRAM_ENCOUNTER_TYPE_UUID_DEFAULT_VALUE));
    List<Encounter> encounters =
        Context.getService(CFLEncounterService.class)
            .getEncountersByPatientAndType(patient, hivProgramEncounterType);
    return encounters.stream().max(Comparator.comparing(Encounter::getEncounterId));
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
}
