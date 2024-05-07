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
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.htmlformentry.model.RefillVisitData;
import org.openmrs.module.cflcore.api.htmlformentry.service.MedicalVisitNoteService;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RefillVisitNoteFunctions {

  private static final String REFILL_VISIT_NOTE_ENCOUNTER_TYPE_UUID =
      "bce100cf-db7f-496a-968f-e0f686397912";

  private static final String PILLS_LEFT_CONCEPT_UUID = "7f1693a5-4223-4a75-9eb7-8d547e6ec677";

  private static final String PILLS_MISSED_CONCEPT_UUID = "977e3982-fe6b-4abf-aaa1-8c1fbfb5f058";

  private static final String ADHERENCE_SCORE_CONCEPT_UUID = "ff16180b-db7e-4423-a925-b29c1b1f2c0f";

  public List<Encounter> getAllRefillEncounters(Patient patient) {
    EncounterService encounterService = Context.getEncounterService();
    EncounterSearchCriteria encounterSearchCriteria =
        new EncounterSearchCriteriaBuilder()
            .setPatient(patient)
            .setEncounterTypes(
                Collections.singleton(
                    encounterService.getEncounterTypeByUuid(
                        (REFILL_VISIT_NOTE_ENCOUNTER_TYPE_UUID))))
            .createEncounterSearchCriteria();

    return encounterService.getEncounters(encounterSearchCriteria);
  }

  public Encounter getPreviousOccurredRefillEncounter(Encounter baseEncounter, Patient patient) {
    List<Encounter> allEncounters = getAllRefillEncounters(patient);
    Date currentEncounterDate = getCurrentEncounterOrVisitDate(baseEncounter);
    Optional<Encounter> closestPastEncounter =
        allEncounters.stream()
            .filter(encounter -> encounter.getEncounterDatetime().before(currentEncounterDate))
            .max(Comparator.comparing(Encounter::getEncounterDatetime));

    return closestPastEncounter.orElse(null);
  }

  public String getRefillVisitData(Encounter currentEncounter, Patient patient) {
    RefillVisitData.EncounterData currentEncounterData = buildEncounterData(currentEncounter);
    Encounter lastEncounter = getPreviousOccurredRefillEncounter(currentEncounter, patient);
    RefillVisitData.EncounterData lastEncounterData = buildEncounterData(lastEncounter);

    RefillVisitData refillVisitData = new RefillVisitData(currentEncounterData, lastEncounterData);

    try {
      return new ObjectMapper().writeValueAsString(refillVisitData);
    } catch (IOException ex) {
      return null;
    }
  }

  private RefillVisitData.EncounterData buildEncounterData(Encounter encounter) {
    RefillVisitData.EncounterData encounterData = new RefillVisitData.EncounterData();

    if (encounter == null) {
      encounterData.setEncounterDate(null);
      encounterData.setDrugData(null);
    } else {
      encounterData.setEncounterDate(
          DateUtil.convertDate(encounter.getEncounterDatetime(), "dd-MM-yyyy"));
      List<RefillVisitData.DrugData> drugDataList = new ArrayList<>();
      Set<Order> orders =
          encounter.getOrders().stream()
              .filter(order -> !order.getVoided())
              .collect(Collectors.toSet());
      MedicalVisitNoteService medicalVisitNoteService =
          Context.getService(MedicalVisitNoteService.class);
      for (Order order : orders) {
        DrugOrder drugOrder = (DrugOrder) order;

        RefillVisitData.DrugData drugData =
            new RefillVisitData.DrugData(
                drugOrder.getDrug().getName(),
                drugOrder.getQuantity(),
                drugOrder.getNumRefills(),
                drugOrder.getDuration(),
                drugOrder.getDurationUnits().getConceptId(),
                drugOrder.getRoute().getConceptId());

        Optional<Obs> drugNameObs =
            encounter.getAllObs().stream()
                .filter(
                    obs ->
                        StringUtils.equalsIgnoreCase(
                            obs.getValueText(), drugOrder.getDrug().getName()))
                .findFirst();
        if (drugNameObs.isPresent()) {
          Obs pillsLeftObs =
              medicalVisitNoteService.findObsByConceptAndGroup(
                  encounter.getAllObs(), PILLS_LEFT_CONCEPT_UUID, drugNameObs.get().getObsGroup());
          Obs pillsMissedObs =
              medicalVisitNoteService.findObsByConceptAndGroup(
                  encounter.getAllObs(),
                  PILLS_MISSED_CONCEPT_UUID,
                  drugNameObs.get().getObsGroup());
          Obs adherenceScoreObs =
              medicalVisitNoteService.findObsByConceptAndGroup(
                  encounter.getAllObs(),
                  ADHERENCE_SCORE_CONCEPT_UUID,
                  drugNameObs.get().getObsGroup());
          drugData.setPillsLeft(pillsLeftObs != null ? pillsLeftObs.getValueNumeric() : null);
          drugData.setPillsMissed(pillsMissedObs != null ? pillsMissedObs.getValueNumeric() : null);
          drugData.setAdherenceScore(
              adherenceScoreObs != null ? adherenceScoreObs.getValueNumeric() : null);
        }

        drugDataList.add(drugData);
      }
      encounterData.setDrugData(drugDataList);
    }

    return encounterData;
  }

  private Date getCurrentEncounterOrVisitDate(Encounter encounter) {
    if (encounter == null) {
      return DateUtil.getDateWithTimeOfDay(DateUtil.now(), "00:00", DateUtil.getSystemTimeZone());
    }

    return encounter.getEncounterDatetime();
  }
}
