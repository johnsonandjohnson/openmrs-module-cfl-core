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

public class PatientHeaderFieldDTO {

  private String label;

  private String value;

  private String type;

  private String format;

  private boolean mainTitleField;

  public PatientHeaderFieldDTO(
      String label, String value, String type, String format, boolean mainTitleField) {
    this.label = label;
    this.value = value;
    this.type = type;
    this.format = format;
    this.mainTitleField = mainTitleField;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public boolean isMainTitleField() {
    return mainTitleField;
  }

  public void setMainTitleField(boolean mainTitleField) {
    this.mainTitleField = mainTitleField;
  }
}
