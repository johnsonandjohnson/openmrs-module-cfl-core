/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.htmlformentry.model;

import java.util.Date;

public class Interruption {

  public static final String INTERRUPTION_DETAILS_GROUP_CONCEPT_UUID =
      "f2b32e5c-3e18-47af-b84d-8d2994361eb5";

  public static final String INTERRUPTION_START_DATE_CONCEPT_UUID =
      "dca99985-a303-4418-a440-d2af44f62c4b";

  public static final String INTERRUPTION_REASON_CONCEPT_UUID =
      "30d09891-6bae-492c-b45b-ebe142837497";

  public static final String INTERRUPTION_RESTART_DATE_CONCEPT_UUID =
      "d9cd2810-d600-47c4-b29c-234c72514b9e";

  private Date startDate;

  private String reason;

  private Date restartDate;

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Date getRestartDate() {
    return restartDate;
  }

  public void setRestartDate(Date restartDate) {
    this.restartDate = restartDate;
  }
}
