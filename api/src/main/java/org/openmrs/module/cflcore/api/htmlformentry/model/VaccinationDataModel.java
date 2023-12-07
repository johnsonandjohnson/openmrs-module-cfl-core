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

public class VaccinationDataModel {

  private String vaccinationConceptUuid;

  private String vaccinationDateReceivedConceptUuid;

  private String vaccinationLotNumberConceptUuid;

  public VaccinationDataModel(
      String vaccinationConceptUuid,
      String vaccinationDateReceivedConceptUuid,
      String vaccinationLotNumberConceptUuid) {
    this.vaccinationConceptUuid = vaccinationConceptUuid;
    this.vaccinationDateReceivedConceptUuid = vaccinationDateReceivedConceptUuid;
    this.vaccinationLotNumberConceptUuid = vaccinationLotNumberConceptUuid;
  }

  public String getVaccinationConceptUuid() {
    return vaccinationConceptUuid;
  }

  public void setVaccinationConceptUuid(String vaccinationConceptUuid) {
    this.vaccinationConceptUuid = vaccinationConceptUuid;
  }

  public String getVaccinationDateReceivedConceptUuid() {
    return vaccinationDateReceivedConceptUuid;
  }

  public void setVaccinationDateReceivedConceptUuid(String vaccinationDateReceivedConceptUuid) {
    this.vaccinationDateReceivedConceptUuid = vaccinationDateReceivedConceptUuid;
  }

  public String getVaccinationLotNumberConceptUuid() {
    return vaccinationLotNumberConceptUuid;
  }

  public void setVaccinationLotNumberConceptUuid(String vaccinationLotNumberConceptUuid) {
    this.vaccinationLotNumberConceptUuid = vaccinationLotNumberConceptUuid;
  }
}
