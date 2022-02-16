package org.openmrs.module.cfl.api.htmlformentry.model;

import java.util.Date;
import java.util.List;

public class Treatment {

  public static final String TREATMENT_DETAILS_GROUP_CONCEPT_UUID =
      "7e08e42b-744c-4a33-8a59-e0ec735ffd59";

  public static final String TREATMENT_START_DATE_CONCEPT_UUID =
      "ddfdf4fa-1d0a-40ca-8930-ad914bd07b23";

  public static final String TREATMENT_STOP_DATE_CONCEPT_UUID =
      "806195ad-9f5c-42f0-ad9f-736a21a1dac1";

  public static final String TREATMENT_REGIMEN_CONCEPT_UUID =
      "c7e17ce8-d832-4349-a326-b20f5ae7f432";

  public static final String TREATMENT_REASON_CONCEPT_UUID = "324f28ab-cfa7-46ac-99f3-c7d928ba5e79";

  private Date startDate;

  private Date stopDate;

  private String regimenName;

  private String reason;

  private List<Interruption> interruptions;

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getStopDate() {
    return stopDate;
  }

  public void setStopDate(Date stopDate) {
    this.stopDate = stopDate;
  }

  public String getRegimenName() {
    return regimenName;
  }

  public void setRegimenName(String regimenName) {
    this.regimenName = regimenName;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public List<Interruption> getInterruptions() {
    return interruptions;
  }

  public void setInterruptions(List<Interruption> interruptions) {
    this.interruptions = interruptions;
  }
}
