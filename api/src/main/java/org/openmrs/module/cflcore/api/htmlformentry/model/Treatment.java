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

  public static final String CURRENT_REGIMEN_CONCEPT_UUID = "20fb8dc4-b958-4c88-b9a7-96ea36b4b0ef";

  public static final String TREATMENT_REASON_CONCEPT_UUID = "324f28ab-cfa7-46ac-99f3-c7d928ba5e79";

  public static final String STOP_TREATMENT_REASON_CONCEPT_UUID =
      "9c65e0a7-8c12-46d4-8ad2-da1aa37b8276";

  private Date startDate;

  private Date stopDate;

  private String regimenName;

  private String reason;

  private boolean isCurrent;

  private List<Interruption> interruptions;

  private String stopTreatmentReason;

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

  public boolean isCurrent() {
    return isCurrent;
  }

  public void setCurrent(boolean current) {
    isCurrent = current;
  }

  public List<Interruption> getInterruptions() {
    return interruptions;
  }

  public void setInterruptions(List<Interruption> interruptions) {
    this.interruptions = interruptions;
  }

  public String getStopTreatmentReason() {
    return stopTreatmentReason;
  }

  public void setStopTreatmentReason(String stopTreatmentReason) {
    this.stopTreatmentReason = stopTreatmentReason;
  }
}
