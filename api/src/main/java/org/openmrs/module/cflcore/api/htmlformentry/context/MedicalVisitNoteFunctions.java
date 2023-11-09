/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.context;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.MedicalVisitNoteAllData;
import org.openmrs.module.cflcore.api.htmlformentry.service.MedicalVisitNoteService;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MedicalVisitNoteFunctions {

  private static final Logger LOGGER = LoggerFactory.getLogger(MedicalVisitNoteFunctions.class);

  private static final String MEDICAL_VISIT_NOTE_ENCOUNTER_TYPE_UUID =
      "968789ca-e9c4-492f-b1dc-101fd1aa2026";

  private static final String EMPTY_ARRAY_JSON = "[]";

  private final MedicalVisitNoteService medicalVisitNoteService;

  MedicalVisitNoteFunctions(MedicalVisitNoteService medicalVisitNoteService) {
    this.medicalVisitNoteService = medicalVisitNoteService;
  }

  public String getPastEncounterData(Encounter baseEncounter, Patient patient) {
    List<Encounter> pastEncounters = getPastMedicalVisitNoteEncounters(baseEncounter, patient);
    List<MedicalVisitNoteAllData> medicalVisitNoteAllData = new ArrayList<>();
    for (Encounter encounter : pastEncounters) {
      medicalVisitNoteAllData.add(
          new MedicalVisitNoteAllData(
              encounter.getUuid(),
              encounter.getEncounterDatetime(),
              medicalVisitNoteService.getVitalData(encounter),
              medicalVisitNoteService.getReviewMedicationData(encounter),
              medicalVisitNoteService.getPhysicalExaminationData(encounter),
              medicalVisitNoteService.getProgressNoteData(encounter),
              medicalVisitNoteService.getVaccinationData(encounter),
              medicalVisitNoteService.getLaboratoryData(encounter)));
    }

    try {
      return new ObjectMapper().writeValueAsString(medicalVisitNoteAllData);
    } catch (IOException e) {
      LOGGER.error("Unable to write medical visit data objects into JSON string");
      return EMPTY_ARRAY_JSON;
    }
  }

  public String getPreviousConsultationDate(Encounter baseEncounter, Patient patient) {
    List<Encounter> pastMedicalVisitEncounters =
        getPastMedicalVisitNoteEncounters(baseEncounter, patient);

    Optional<Encounter> latestPastMedicalVisitEncounter =
        pastMedicalVisitEncounters.stream().findFirst();

    if (latestPastMedicalVisitEncounter.isPresent()) {
      return latestPastMedicalVisitEncounter.get().getEncounterDatetime().toString();
    } else {
      return StringUtils.EMPTY;
    }
  }

  public List<Encounter> getPastMedicalVisitNoteEncounters(
      Encounter baseEncounter, Patient patient) {
    EncounterService encounterService = Context.getEncounterService();
    Date currentEncounterDate = getCurrentEncounterDate(baseEncounter);

    EncounterSearchCriteria encounterSearchCriteria =
        new EncounterSearchCriteriaBuilder()
            .setPatient(patient)
            .setEncounterTypes(
                Collections.singleton(
                    encounterService.getEncounterTypeByUuid(
                        (MEDICAL_VISIT_NOTE_ENCOUNTER_TYPE_UUID))))
            .createEncounterSearchCriteria();

    return encounterService.getEncounters(encounterSearchCriteria).stream()
        .filter(
            enc ->
                enc.getEncounterDatetime().toInstant().isBefore(currentEncounterDate.toInstant()))
        .sorted(Comparator.comparing(Encounter::getEncounterDatetime).reversed())
        .collect(Collectors.toList());
  }

  private Date getCurrentEncounterDate(Encounter encounter) {
    if (encounter == null) {
      return DateUtil.getDateWithTimeOfDay(DateUtil.now(), "00:00", DateUtil.getSystemTimeZone());
    }

    return encounter.getEncounterDatetime();
  }
}
