package org.openmrs.module.cflcore.domain.criteria;

public class PatientFlagsOverviewCriteria {

  private String locationUuid;

  private String flagName;

  private String query;

  private String patientStatus;

  public PatientFlagsOverviewCriteria() {
  }

  public PatientFlagsOverviewCriteria(String locationUuid, String flagName, String query,
      String patientStatus) {
    this.locationUuid = locationUuid;
    this.flagName = flagName;
    this.query = query;
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

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getPatientStatus() {
    return patientStatus;
  }

  public void setPatientStatus(String patientStatus) {
    this.patientStatus = patientStatus;
  }
}
