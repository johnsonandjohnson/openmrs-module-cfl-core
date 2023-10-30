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

public class VaccinationDetailsDTO {

  public static final String VACCINATION_CONCEPT_UUID = "abbdfb23-ba87-482c-b370-62dd1e1ecfaf";

  private String name;

  private String dateReceived;

  private String lotNumber;

  public VaccinationDetailsDTO(String name, String dateReceived, String lotNumber) {
    this.name = name;
    this.dateReceived = dateReceived;
    this.lotNumber = lotNumber;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDateReceived() {
    return dateReceived;
  }

  public void setDateReceived(String dateReceived) {
    this.dateReceived = dateReceived;
  }

  public String getLotNumber() {
    return lotNumber;
  }

  public void setLotNumber(String lotNumber) {
    this.lotNumber = lotNumber;
  }
}
