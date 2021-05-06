package org.openmrs.module.cfl.fragment.controller.dashboardwidgets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.cfl.api.contract.ProgramConfig;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramFragmentController {

    private static final String PATIENT_ID = "patientId";
    private static final String ENCOUNTER_ID_ATTR_NAME = "encounterId";
    private static final String PROGRAMS_LIST_ATTR_NAME = "programsList";
    private static final String NAME_ATTR_NAME = "name";
    private static final String IS_ENROLLED_ATTR_NAME = "isEnrolled";
    private static final String DATE_ENROLLED_ATTR_NAME = "dateEnrolled";
    private static final String DATE_COMPLETED_ATTR_NAME = "dateCompleted";
    private static final String IS_VOIDED_ATTR_NAME = "isVoided";
    private static final String PATIENT_PROGRAM_ID_ATTR_NAME = "patientProgramId";
    private static final String PROGRAM_CONFIG_ATTR_NAME = "programConfig";
    private static final String CURRENT_LABEL_TEXT = "Current";
    private static final String PROGRAM_MAP_LIST = "programMapList";

    public void controller(FragmentModel model, FragmentConfiguration configuration,
                           @RequestParam(PATIENT_ID) Patient patient,
                           @SpringBean("encounterService") EncounterService encounterService,
                           @SpringBean("programWorkflowService") ProgramWorkflowService programService) {

        List<Program> programs = programService.getAllPrograms(false);
        List<PatientProgram> patientPrograms = programService.getPatientPrograms(patient, null, null,
                null, null, null, false);

        List<ProgramConfig> programConfigs = getProgramConfigs(configuration);

        List<Map<String, Object>> programsList = new ArrayList<Map<String, Object>>();
        for (Program program : programs) {
            Map<String, Object> programMap = new HashMap<String, Object>();
            programMap.put(NAME_ATTR_NAME, program.getName());
            for (PatientProgram patientProgram : patientPrograms) {
                if (patientProgram.getProgram().equals(program)) {
                    programMap.put(IS_ENROLLED_ATTR_NAME, patientProgram.getActive());
                    programMap.put(DATE_ENROLLED_ATTR_NAME, patientProgram.getDateEnrolled());
                    programMap.put(DATE_COMPLETED_ATTR_NAME, null != patientProgram.getDateCompleted()
                            ? patientProgram.getDateCompleted() : CURRENT_LABEL_TEXT);
                    programMap.put(IS_VOIDED_ATTR_NAME, patientProgram.isVoided());
                    model.addAttribute(PATIENT_PROGRAM_ID_ATTR_NAME, patientProgram.getPatientProgramId());
                }
            }
            programMap.put(ENCOUNTER_ID_ATTR_NAME, findProperEncounterIdByProgramNameAndPatient(
                    programConfigs, program.getName(), patient, encounterService));
            programMap.put(PROGRAM_CONFIG_ATTR_NAME, findProgramConfigByName(programConfigs, program.getName()));
            programsList.add(programMap);
        }

        model.addAttribute(PROGRAMS_LIST_ATTR_NAME, programsList);
        model.addAttribute(PATIENT_ID, patient.getUuid());
    }

    private List<ProgramConfig> getProgramConfigs(FragmentConfiguration configuration) {
        List<ProgramConfig> programConfigs = new ArrayList<ProgramConfig>();
        Gson gson = new Gson();
        String jsonArrayString = configuration.get(PROGRAM_MAP_LIST).toString();
        JsonArray jsonArray = new JsonParser().parse(jsonArrayString).getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            programConfigs.add(gson.fromJson(jsonElement.getAsJsonObject().toString(), ProgramConfig.class));
        }
        return programConfigs;
    }

    private Integer findProperEncounterIdByProgramNameAndPatient(List<ProgramConfig> configs, String programName,
                                                                 Patient patient, EncounterService encounterService) {
        Integer encounterId = null;
        ProgramConfig programConfig = findProgramConfigByName(configs, programName);
        if (programConfig != null) {
            EncounterType encounterType = encounterService.getEncounterTypeByUuid(
                    programConfig.getProgramFormEncounterTypeUuid());
            List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null,
                    null, Arrays.asList(encounterType), null, null, null, false);
            encounterId = getRelatedEncounterId(encounters);
        }
        return encounterId;
    }

    private ProgramConfig findProgramConfigByName(List<ProgramConfig> configs, String configName) {
        ProgramConfig programConfig = null;
        for (ProgramConfig config : configs) {
            if (StringUtils.equalsIgnoreCase(config.getName(), configName)) {
                programConfig = config;
                break;
            }
        }
        return programConfig;
    }

    private Integer getRelatedEncounterId(List<Encounter> encounters) {
        Integer encounterId = null;
        if (encounters.size() > 0) {
            encounterId = encounters.get(encounters.size() - 1).getId();
        }
        return encounterId;
    }
}
