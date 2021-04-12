package org.openmrs.module.cfl.fragment.controller.dashboardwidgets;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramFragmentController {

    private static final String PATIENT_ID = "patientId";
    private static final String ENCOUNTER_ID_ATTR_NAME = "encounterId";
    private static final String PROGRAMS_LIST_ATTR_NAME = "programsList";
    private static final String ENCOUNTER_TYPE_UUID = "encounterTypeUuid";
    private static final String NAME_ATTR_NAME = "name";
    private static final String IS_ENROLLED_ATTR_NAME = "isEnrolled";
    private static final String DATE_ENROLLED_ATTR_NAME = "dateEnrolled";
    private static final String DATE_COMPLETED_ATTR_NAME = "dateCompleted";
    private static final String IS_VOIDED_ATTR_NAME = "isVoided";
    private static final String PATIENT_PROGRAM_ID_ATTR_NAME = "patientProgramId";
    private static final String CURRENT_LABEL_TEXT = "Current";

    public void controller(FragmentModel model, FragmentConfiguration configuration,
                           @RequestParam(PATIENT_ID) Patient patient,
                           @SpringBean("encounterService") EncounterService encounterService,
                           @SpringBean("programWorkflowService") ProgramWorkflowService programService) {

        List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(
                (configuration.get(ENCOUNTER_TYPE_UUID).toString()));
        encounterTypes.add(encounterType);
        List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null,
                null, encounterTypes, null, null, null, false);

        List<Program> programs = programService.getAllPrograms(false);
        List<PatientProgram> patientPrograms = programService.getPatientPrograms(patient, null, null,
                null, null, null, false);

        List<Map<String, Object>> programsList = new ArrayList<Map<String, Object>>();
        for (Program program : programs) {
            Map<String, Object> programMap = new HashMap<String, Object>();
            boolean existsNotActiveProgram = false;
            programMap.put(NAME_ATTR_NAME, program.getName());
            for (PatientProgram patientProgram : patientPrograms) {
                if (patientProgram.getProgram().equals(program)) {
//                    Map<String, Object> programMap2 = new HashMap<String, Object>(programMap);
//                    programMap2.put(IS_ENROLLED_ATTR_NAME, patientProgram.getActive());
//                    programMap2.put(DATE_ENROLLED_ATTR_NAME, patientProgram.getDateEnrolled());
//                    programMap2.put(DATE_COMPLETED_ATTR_NAME, null != patientProgram.getDateCompleted()
//                            ? patientProgram.getDateCompleted() : CURRENT_LABEL_TEXT);
//                    programMap2.put(IS_VOIDED_ATTR_NAME, patientProgram.isVoided());
//                    model.addAttribute(PATIENT_PROGRAM_ID_ATTR_NAME, patientProgram.getPatientProgramId());
//                    programsList.add(programMap2);
                    programMap.put(IS_ENROLLED_ATTR_NAME, patientProgram.getActive());
                    programMap.put(DATE_ENROLLED_ATTR_NAME, patientProgram.getDateEnrolled());
                    programMap.put(DATE_COMPLETED_ATTR_NAME, null != patientProgram.getDateCompleted()
                            ? patientProgram.getDateCompleted() : CURRENT_LABEL_TEXT);
                    programMap.put(IS_VOIDED_ATTR_NAME, patientProgram.isVoided());
                    model.addAttribute(PATIENT_PROGRAM_ID_ATTR_NAME, patientProgram.getPatientProgramId());
//                    programsList.add(programMap);
//                    if (!patientProgram.getActive()) {
//                        existsNotActiveProgram = true;
//                    }
                }
            }
            programsList.add(programMap);
//            if (!existsNotActiveProgram) {
//                programsList.add(programMap);
//            }
        }

        model.addAttribute(PROGRAMS_LIST_ATTR_NAME, programsList);
        model.addAttribute(ENCOUNTER_ID_ATTR_NAME, getRelatedEncounterId(encounters));
        model.addAttribute(PATIENT_ID, patient.getUuid());
    }

    private Integer getRelatedEncounterId(List<Encounter> encounters) {
        Integer encounterId = null;
        if (encounters.size() > 0) {
            encounterId = encounters.get(encounters.size() - 1).getId();
        }
        return encounterId;
    }

}
