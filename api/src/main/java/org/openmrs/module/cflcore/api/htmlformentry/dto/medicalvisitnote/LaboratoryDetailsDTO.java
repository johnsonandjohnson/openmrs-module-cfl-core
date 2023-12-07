/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.dto.medicalvisitnote;

public class LaboratoryDetailsDTO {

  private String testName;

  private String dateDone;

  private String result;

  private String symbol;

  private String resultUnit;

  private String clearance;

  public LaboratoryDetailsDTO(
      String testName,
      String dateDone,
      String result,
      String symbol,
      String resultUnit,
      String clearance) {
    this.testName = testName;
    this.dateDone = dateDone;
    this.result = result;
    this.symbol = symbol;
    this.resultUnit = resultUnit;
    this.clearance = clearance;
  }

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public String getDateDone() {
    return dateDone;
  }

  public void setDateDone(String dateDone) {
    this.dateDone = dateDone;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getResultUnit() {
    return resultUnit;
  }

  public void setResultUnit(String resultUnit) {
    this.resultUnit = resultUnit;
  }

  public String getClearance() {
    return clearance;
  }

  public void setClearance(String clearance) {
    this.clearance = clearance;
  }
}
