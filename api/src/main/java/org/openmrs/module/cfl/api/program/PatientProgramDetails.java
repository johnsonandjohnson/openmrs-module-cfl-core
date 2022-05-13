/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.program;

import org.openmrs.Patient;

import java.util.Date;

public class PatientProgramDetails {

  private Patient patient;

  private String programName;

  private boolean isEnrolled;

  private Date dateEnrolled;

  private Date dateCompleted;

  private boolean isVoided;

  private Integer encounterId;

  private ProgramConfig programConfig;

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  public String getProgramName() {
    return programName;
  }

  public void setProgramName(String programName) {
    this.programName = programName;
  }

  public boolean isEnrolled() {
    return isEnrolled;
  }

  public void setEnrolled(boolean enrolled) {
    isEnrolled = enrolled;
  }

  public Date getDateEnrolled() {
    return dateEnrolled;
  }

  public void setDateEnrolled(Date dateEnrolled) {
    this.dateEnrolled = dateEnrolled;
  }

  public Date getDateCompleted() {
    return dateCompleted;
  }

  public void setDateCompleted(Date dateCompleted) {
    this.dateCompleted = dateCompleted;
  }

  public boolean isVoided() {
    return isVoided;
  }

  public void setVoided(boolean voided) {
    isVoided = voided;
  }

  public Integer getEncounterId() {
    return encounterId;
  }

  public void setEncounterId(Integer encounterId) {
    this.encounterId = encounterId;
  }

  public ProgramConfig getProgramConfig() {
    return programConfig;
  }

  public void setProgramConfig(ProgramConfig programConfig) {
    this.programConfig = programConfig;
  }
}
