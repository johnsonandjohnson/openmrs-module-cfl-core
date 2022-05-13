/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.dto;

import java.util.List;

/** ImportDataResultDTO class - represents result data after importing file with addresses */
public class ImportDataResultDTO {

  int numberOfTotalRecords;

  int numberOfInvalidRecords;

  List<String> invalidRecords;

  public ImportDataResultDTO() {}

  public ImportDataResultDTO(
      int numberOfTotalRecords, int numberOfInvalidRecords, List<String> invalidRecords) {
    this.numberOfTotalRecords = numberOfTotalRecords;
    this.numberOfInvalidRecords = numberOfInvalidRecords;
    this.invalidRecords = invalidRecords;
  }

  public int getNumberOfTotalRecords() {
    return numberOfTotalRecords;
  }

  public void setNumberOfTotalRecords(int numberOfTotalRecords) {
    this.numberOfTotalRecords = numberOfTotalRecords;
  }

  public int getNumberOfInvalidRecords() {
    return numberOfInvalidRecords;
  }

  public void setNumberOfInvalidRecords(int numberOfInvalidRecords) {
    this.numberOfInvalidRecords = numberOfInvalidRecords;
  }

  public List<String> getInvalidRecords() {
    return invalidRecords;
  }

  public void setInvalidRecords(List<String> invalidRecords) {
    this.invalidRecords = invalidRecords;
  }
}
