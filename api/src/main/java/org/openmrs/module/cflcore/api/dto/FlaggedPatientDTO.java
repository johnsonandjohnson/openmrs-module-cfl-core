/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.dto;

import org.openmrs.module.cflcore.db.PatientFlagsOverviewQueryBuilder;

public class FlaggedPatientDTO {

  private String patientIdentifier;

  private String patientName;

  private String phoneNumber;

  private String patientStatus;

  private String patientUuid;

  public FlaggedPatientDTO(Object[] results) {
    this.patientIdentifier = (String) results[PatientFlagsOverviewQueryBuilder.PATIENT_IDENTIFIER_RESULT_INDEX];
    this.patientName = (String) results[PatientFlagsOverviewQueryBuilder.PATIENT_NAME_RESULT_INDEX];
    this.phoneNumber = (String) results[PatientFlagsOverviewQueryBuilder.PHONE_NUMBER_RESULT_INDEX];
    this.patientStatus = (String) results[PatientFlagsOverviewQueryBuilder.PATIENT_STATUS_RESULT_INDEX];
    this.patientUuid = (String) results[PatientFlagsOverviewQueryBuilder.PATIENT_UUID_RESULT_INDEX];
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
