package org.openmrs.module.cflcore.api.dto;

public class PatientFlagsOverviewDTO {

  private static final Integer PATIENT_IDENTIFIER_RESULT_INDEX = 0;

  private static final Integer PATIENT_NAME_RESULT_INDEX = 1;

  private static final Integer PHONE_NUMBER_RESULT_INDEX = 2;

  private static final Integer PATIENT_STATUS_RESULT_INDEX = 3;

  private static final Integer PATIENT_UUID_RESULT_INDEX = 4;

  private String patientIdentifier;

  private String patientName;

  private String phoneNumber;

  private String patientStatus;

  private String patientUuid;

  public PatientFlagsOverviewDTO(Object[] results) {
    this.patientIdentifier = (String) results[PATIENT_IDENTIFIER_RESULT_INDEX];
    this.patientName = (String) results[PATIENT_NAME_RESULT_INDEX];
    this.phoneNumber = (String) results[PHONE_NUMBER_RESULT_INDEX];
    this.patientStatus = (String) results[PATIENT_STATUS_RESULT_INDEX];
    this.patientUuid = (String) results[PATIENT_UUID_RESULT_INDEX];
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

  public String getPatientUuid() {
    return patientUuid;
  }

  public void setPatientUuid(String patientUuid) {
    this.patientUuid = patientUuid;
  }
}
