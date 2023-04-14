/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.cflcore.api.program.PatientProgramDetails;
import org.openmrs.module.cflcore.api.program.ProgramConfig;
import org.openmrs.module.cflcore.api.service.PatientProgramDetailsService;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PatientProgramDetailsServiceImpl implements PatientProgramDetailsService {
  private static final String PROGRAM_MAP_LIST_APP_ATTRIBUTE_NAME = "programMapList";

  @Override
  public List<PatientProgramDetails> getPatientProgramsDetails(
      Patient patient, FragmentConfiguration configuration) {
    List<ProgramConfig> programConfigs = getProgramConfigs(configuration);

    List<PatientProgramDetails> patientProgramDetailsList = new ArrayList<>();
    for (Program program : Context.getProgramWorkflowService().getAllPrograms(false)) {
      buildPatientProgramDetails(patient, program, programConfigs)
          .ifPresent(patientProgramDetailsList::add);
    }

    return patientProgramDetailsList;
  }

  private List<ProgramConfig> getProgramConfigs(FragmentConfiguration configuration) {
    List<ProgramConfig> programConfigs = new ArrayList<>();
    List<Object> programMapList =
        (ArrayList) configuration.get(PROGRAM_MAP_LIST_APP_ATTRIBUTE_NAME);
    ObjectMapper objectMapper = new ObjectMapper();
    programMapList.forEach(
        programConfig ->
            programConfigs.add(objectMapper.convertValue(programConfig, ProgramConfig.class)));

    return programConfigs;
  }

  private Optional<PatientProgramDetails> buildPatientProgramDetails(
      Patient patient, Program program, List<ProgramConfig> programConfigs) {
    final Optional<ProgramConfig> programConfigOpt =
        findProgramConfigByName(programConfigs, program.getName());

    return programConfigOpt.map(
        programConfig -> {
          PatientProgramDetails patientProgramDetails = new PatientProgramDetails();
          patientProgramDetails.setProgramName(program.getName());
          patientProgramDetails.setProgramUuid(program.getUuid());
          patientProgramDetails.setPatient(patient);
          patientProgramDetails.setEncounterId(getRelatedEncounterId(programConfig, patient));
          patientProgramDetails.setProgramConfig(programConfig);
          setAdditionalPatientProgramDetails(patient, program, patientProgramDetails);
          return patientProgramDetails;
        });
  }

  private Integer getRelatedEncounterId(ProgramConfig programConfig, Patient patient) {
    String formUuid = programConfig.getProgramFormUuid();
    Form form = Context.getFormService().getFormByUuid(formUuid);

    if (form == null) {
      throw new CflRuntimeException(String.format("Form with uuid: %s not found", formUuid));
    }

    return getEncounterIdByEncounterSearchCriteria(patient, form.getEncounterType());
  }

  private Integer getEncounterIdByEncounterSearchCriteria(
      Patient patient, EncounterType encounterType) {
    EncounterSearchCriteria searchCriteria =
        new EncounterSearchCriteriaBuilder()
            .setPatient(patient)
            .setEncounterTypes(Collections.singletonList(encounterType))
            .createEncounterSearchCriteria();

    return Context.getEncounterService().getEncounters(searchCriteria).stream()
        .map(Encounter::getEncounterId)
        .reduce((first, second) -> second)
        .orElse(null);
  }

  private Optional<ProgramConfig> findProgramConfigByName(
      List<ProgramConfig> configs, String configName) {
    return configs.stream()
        .filter(config -> StringUtils.equalsIgnoreCase(config.getName(), configName))
        .findFirst();
  }

  private void setAdditionalPatientProgramDetails(
      Patient patient, Program program, PatientProgramDetails programDetails) {
    List<PatientProgram> patientPrograms =
        Context.getProgramWorkflowService()
            .getPatientPrograms(patient, null, null, null, null, null, false);
    for (PatientProgram patientProgram : patientPrograms) {
      if (patientProgram.getProgram().equals(program)) {
        programDetails.setEnrolled(patientProgram.getActive());
        programDetails.setDateEnrolled(patientProgram.getDateEnrolled());
        programDetails.setDateCompleted(patientProgram.getDateCompleted());
        programDetails.setVoided(patientProgram.getVoided());
      }
    }
  }
}
