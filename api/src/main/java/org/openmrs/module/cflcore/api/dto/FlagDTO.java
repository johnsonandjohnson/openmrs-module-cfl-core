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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.patientflags.Flag;

public class FlagDTO {

  private String name;

  private String uuid;

  private String priority;

  public FlagDTO(Flag flag) {
    this.name = flag.getName();
    this.uuid = flag.getUuid();
    this.priority = flag.getPriority() != null ? flag.getPriority().getName() : StringUtils.EMPTY;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }
}
