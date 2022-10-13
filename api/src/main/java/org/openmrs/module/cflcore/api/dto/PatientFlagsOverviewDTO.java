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

import java.util.List;

public class PatientFlagsOverviewDTO {

  private List<FlaggedPatientDTO> flaggedPatients;

  private Long totalCount;

  public PatientFlagsOverviewDTO(List<FlaggedPatientDTO> flaggedPatients, Long totalCount) {
    this.flaggedPatients = flaggedPatients;
    this.totalCount = totalCount;
  }

  public List<FlaggedPatientDTO> getFlaggedPatients() {
    return flaggedPatients;
  }

  public void setFlaggedPatients(
      List<FlaggedPatientDTO> flaggedPatients) {
    this.flaggedPatients = flaggedPatients;
  }

  public Long getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Long totalCount) {
    this.totalCount = totalCount;
  }
}
