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
import org.openmrs.Visit;
import org.openmrs.module.cflcore.api.htmlformentry.model.Regimen;
import org.openmrs.module.cflcore.api.htmlformentry.model.RegimenDrug;
import org.openmrs.module.cflcore.api.htmlformentry.model.Treatment;
import org.openmrs.module.cflcore.api.htmlformentry.service.TreatmentService;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TreatmentFunctions {
  private static final Logger LOGGER = LoggerFactory.getLogger(TreatmentFunctions.class);
  private static final String EMPTY_ARRAY_JSON = "[]";

  private final FormEntrySession formEntrySession;
  private final TreatmentService treatmentService;

  TreatmentFunctions(FormEntrySession formEntrySession, TreatmentService treatmentService) {
    this.formEntrySession = formEntrySession;
    this.treatmentService = treatmentService;
  }

  public boolean isEncounterDateModificationAllowed() {
    return formEntrySession.getEncounter() == null;
  }

  public boolean isModificationAllowed() {
    // Modification allowed only when there are no orders with date stopped
    return formEntrySession.getEncounter() == null
        || formEntrySession.getEncounter().getOrders().stream()
            .filter(order -> !order.getVoided())
            .noneMatch(order -> order.getDateStopped() != null);
  }

  public String getAllTreatmentsJSON() {
    try {
      return new ObjectMapper()
          .writeValueAsString(treatmentService.getAllTreatments(formEntrySession.getPatient()));
    } catch (IOException e) {
      LOGGER.error("Unable to write treatments object into JSON string");
      return EMPTY_ARRAY_JSON;
    }
  }

  public String getCurrentRegimenName() {
    return treatmentService
        .getCurrentTreatment(formEntrySession.getPatient(), getEncounterDateTime())
        .flatMap(this::getRegimenByTreatment)
        .map(Regimen::getName)
        .orElse("");
  }

  public String getCurrentRegimenDrugDetailsFormRepeatArray() {
    return treatmentService
        .getCurrentTreatment(formEntrySession.getPatient(), getEncounterDateTime())
        .flatMap(this::getRegimenByTreatment)
        .map(Regimen::getRegimenDrugs)
        .map(this::buildRegimenDrugsStringArray)
        .orElse(EMPTY_ARRAY_JSON);
  }

  private Date getEncounterDateTime() {
    if (formEntrySession.getEncounter() != null) {
      return formEntrySession.getEncounter().getEncounterDatetime();
    } else if (formEntrySession.getContext().getVisit() != null) {
      return ((Visit) formEntrySession.getContext().getVisit()).getStartDatetime();
    } else {
      return DateUtil.now();
    }
  }

  private String buildRegimenDrugsStringArray(List<RegimenDrug> regimenDrugs) {
    return regimenDrugs.stream()
        .map(
            regimenDrug ->
                "['".concat(regimenDrug.getDrug().getName().replace(" ", "_").concat("']")))
        .collect(Collectors.joining(","));
  }

  private Optional<Regimen> getRegimenByTreatment(Treatment treatment) {
    List<Regimen> regimens = HtmlFormContextUtil.getRegimensWithDrugs();
    return regimens.stream()
        .filter(
            regimen -> StringUtils.equalsIgnoreCase(regimen.getName(), treatment.getRegimenName()))
        .findFirst();
  }
}
