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

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote.MedicalVisitNoteAllData;
import org.openmrs.module.cflcore.api.htmlformentry.service.MedicalVisitNoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MedicalVisitNoteFunctions {

  private static final Logger LOGGER = LoggerFactory.getLogger(MedicalVisitNoteFunctions.class);

  private static final String EMPTY_ARRAY_JSON = "[]";

  private final MedicalVisitNoteService medicalVisitNoteService;

  MedicalVisitNoteFunctions(MedicalVisitNoteService medicalVisitNoteService) {
    this.medicalVisitNoteService = medicalVisitNoteService;
  }

  public String getPastEncounterData(Encounter currentEncounter) {
    List<Encounter> pastEncounters =
        Context.getEncounterService().getEncountersByPatient(currentEncounter.getPatient()).stream()
            .filter(
                enc ->
                    enc.getEncounterDatetime()
                        .toInstant()
                        .isBefore(currentEncounter.getEncounterDatetime().toInstant()))
            .sorted(Comparator.comparing(Encounter::getEncounterDatetime).reversed())
            .collect(Collectors.toList());

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
}
