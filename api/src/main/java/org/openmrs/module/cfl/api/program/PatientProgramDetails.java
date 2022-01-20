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
