/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefillVisitData {

  private EncounterData currentEncounter;

  private EncounterData lastEncounter;

  public RefillVisitData(EncounterData currentEncounter, EncounterData lastEncounter) {
    this.currentEncounter = currentEncounter;
    this.lastEncounter = lastEncounter;
  }

  public EncounterData getCurrentEncounter() {
    return currentEncounter;
  }

  public void setCurrentEncounter(EncounterData currentEncounter) {
    this.currentEncounter = currentEncounter;
  }

  public EncounterData getLastEncounter() {
    return lastEncounter;
  }

  public void setLastEncounter(EncounterData lastEncounter) {
    this.lastEncounter = lastEncounter;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class EncounterData {

    private String encounterDate;

    private List<DrugData> drugData;

    public EncounterData() {}

    public EncounterData(String encounterDate, List<DrugData> drugData) {
      this.encounterDate = encounterDate;
      this.drugData = drugData;
    }

    public String getEncounterDate() {
      return encounterDate;
    }

    public void setEncounterDate(String encounterDate) {
      this.encounterDate = encounterDate;
    }

    public List<DrugData> getDrugData() {
      return drugData;
    }

    public void setDrugData(List<DrugData> drugData) {
      this.drugData = drugData;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class DrugData {

    private String drugName;

    private Double quantity;

    private Integer refillsNumber;

    private Integer duration;

    private Integer durationUnitsConceptId;

    private Double pillsLeft;

    private Double pillsMissed;

    private Integer administrationRouteConceptId;

    private Double adherenceScore;

    public DrugData(
        String drugName,
        Double quantity,
        Integer refillsNumber,
        Integer duration,
        Integer durationUnitsConceptId,
        Integer administrationRouteConceptId) {
      this.drugName = drugName;
      this.quantity = quantity;
      this.refillsNumber = refillsNumber;
      this.duration = duration;
      this.durationUnitsConceptId = durationUnitsConceptId;
      this.administrationRouteConceptId = administrationRouteConceptId;
    }

    public String getDrugName() {
      return drugName;
    }

    public void setDrugName(String drugName) {
      this.drugName = drugName;
    }

    public Double getQuantity() {
      return quantity;
    }

    public void setQuantity(Double quantity) {
      this.quantity = quantity;
    }

    public Integer getRefillsNumber() {
      return refillsNumber;
    }

    public void setRefillsNumber(Integer refillsNumber) {
      this.refillsNumber = refillsNumber;
    }

    public Integer getDuration() {
      return duration;
    }

    public void setDuration(Integer duration) {
      this.duration = duration;
    }

    public Integer getDurationUnitsConceptId() {
      return durationUnitsConceptId;
    }

    public void setDurationUnitsConceptId(Integer durationUnitsConceptId) {
      this.durationUnitsConceptId = durationUnitsConceptId;
    }

    public Double getPillsLeft() {
      return pillsLeft;
    }

    public void setPillsLeft(Double pillsLeft) {
      this.pillsLeft = pillsLeft;
    }

    public Double getPillsMissed() {
      return pillsMissed;
    }

    public void setPillsMissed(Double pillsMissed) {
      this.pillsMissed = pillsMissed;
    }

    public Integer getAdministrationRouteConceptId() {
      return administrationRouteConceptId;
    }

    public void setAdministrationRouteConceptId(Integer administrationRouteConceptId) {
      this.administrationRouteConceptId = administrationRouteConceptId;
    }

    public Double getAdherenceScore() {
      return adherenceScore;
    }

    public void setAdherenceScore(Double adherenceScore) {
      this.adherenceScore = adherenceScore;
    }
  }
}
