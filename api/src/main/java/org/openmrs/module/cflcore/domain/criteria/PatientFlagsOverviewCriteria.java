package org.openmrs.module.cflcore.domain.criteria;

public class PatientFlagsOverviewCriteria {

  private String locationUuid;

  private String flagName;

  private String patientIdentifier;

  private String patientName;

  private String phoneNumber;

  private String patientStatus;

  public PatientFlagsOverviewCriteria() {
  }

  public PatientFlagsOverviewCriteria(String locationUuid, String flagName,
      String patientIdentifier,
      String patientName, String phoneNumber, String patientStatus) {
    this.locationUuid = locationUuid;
    this.flagName = flagName;
    this.patientIdentifier = patientIdentifier;
    this.patientName = patientName;
    this.phoneNumber = phoneNumber;
    this.patientStatus = patientStatus;
  }

  public String getLocationUuid() {
    return locationUuid;
  }

  public void setLocationUuid(String locationUuid) {
    this.locationUuid = locationUuid;
  }

  public String getFlagName() {
    return flagName;
  }

  public void setFlagName(String flagName) {
    this.flagName = flagName;
  }

  public String getPatientIdentifier() {
    return patientIdentifier;
  }

  public void setPatientIdentifier(String patientIdentifier) {
    this.patientIdentifier = patientIdentifier;
  }

  public String getPatientName() {
    return patientName;
  }

  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPatientStatus() {
    return patientStatus;
  }

  public void setPatientStatus(String patientStatus) {
    this.patientStatus = patientStatus;
  }
}
