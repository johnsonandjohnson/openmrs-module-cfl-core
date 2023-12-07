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

public class LaboratoryDataModel {

  private String studyName;

  private String dateDoneConceptUuid;

  private String resultsConceptUuid;

  private String resultUnitLabel;

  private String symbolConceptUuid;

  private String clearanceConceptUuid;

  public LaboratoryDataModel(
      String studyName,
      String dateDoneConceptUuid,
      String resultsConceptUuid,
      String resultUnitLabel,
      String symbolConceptUuid,
      String clearanceConceptUuid) {
    this.studyName = studyName;
    this.dateDoneConceptUuid = dateDoneConceptUuid;
    this.resultsConceptUuid = resultsConceptUuid;
    this.resultUnitLabel = resultUnitLabel;
    this.symbolConceptUuid = symbolConceptUuid;
    this.clearanceConceptUuid = clearanceConceptUuid;
  }

  public String getStudyName() {
    return studyName;
  }

  public void setStudyName(String studyName) {
    this.studyName = studyName;
  }

  public String getDateDoneConceptUuid() {
    return dateDoneConceptUuid;
  }

  public void setDateDoneConceptUuid(String dateDoneConceptUuid) {
    this.dateDoneConceptUuid = dateDoneConceptUuid;
  }

  public String getResultsConceptUuid() {
    return resultsConceptUuid;
  }

  public void setResultsConceptUuid(String resultsConceptUuid) {
    this.resultsConceptUuid = resultsConceptUuid;
  }

  public String getResultUnitLabel() {
    return resultUnitLabel;
  }

  public void setResultUnitLabel(String resultUnitLabel) {
    this.resultUnitLabel = resultUnitLabel;
  }

  public String getSymbolConceptUuid() {
    return symbolConceptUuid;
  }

  public void setSymbolConceptUuid(String symbolConceptUuid) {
    this.symbolConceptUuid = symbolConceptUuid;
  }

  public String getClearanceConceptUuid() {
    return clearanceConceptUuid;
  }

  public void setClearanceConceptUuid(String clearanceConceptUuid) {
    this.clearanceConceptUuid = clearanceConceptUuid;
  }
}
