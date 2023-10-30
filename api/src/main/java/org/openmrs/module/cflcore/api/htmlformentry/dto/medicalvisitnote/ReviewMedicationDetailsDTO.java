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

public class ReviewMedicationDetailsDTO {

  public static final String MEDICATION_CONCEPT_UUID = "b2afe66d-b5bd-42db-8813-86e514548326";

  public static final String MEDICATION_START_DATE_CONCEPT_UUID =
      "98f980c8-b352-4e5e-9a37-b5b0227317f4";

  public static final String MEDICATION_END_DATE_CONCEPT_UUID =
      "529f345e-d80d-4038-80b4-1fc585afda33";

  private String medication;

  private String startDate;

  private String endDate;

  public ReviewMedicationDetailsDTO(String medication, String startDate, String endDate) {
    this.medication = medication;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getMedication() {
    return medication;
  }

  public void setMedication(String medication) {
    this.medication = medication;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }
}
