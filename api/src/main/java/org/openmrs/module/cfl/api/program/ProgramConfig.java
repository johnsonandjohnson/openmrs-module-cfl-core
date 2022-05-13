/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.program;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramConfig {

  private String name;

  private String programFormUuid;

  private String discontinuationFormUuid;

  private Boolean enterModeOnly;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProgramFormUuid() {
    return programFormUuid;
  }

  public void setFormUuid(String programFormUuid) {
    this.programFormUuid = programFormUuid;
  }

  public String getDiscontinuationFormUuid() {
    return discontinuationFormUuid;
  }

  public void setDiscontinuationFormUuid(String discontinuationFormUuid) {
    this.discontinuationFormUuid = discontinuationFormUuid;
  }

  public Boolean getEnterModeOnly() {
    return enterModeOnly;
  }

  public void setEnterModeOnly(Boolean enterModeOnly) {
    this.enterModeOnly = enterModeOnly;
  }
}
