/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.patienheader.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientHeaderConfigModel {

  private List<PatientHeaderFieldModel> titleFields;

  private List<PatientHeaderFieldModel> attributeFields;

  private boolean deleteButtonOnPatientDashboardVisible;

  private boolean updateStatusButtonVisible;

  public PatientHeaderConfigModel(
      List<PatientHeaderFieldModel> titleFields,
      List<PatientHeaderFieldModel> attributeFields,
      boolean deleteButtonOnPatientDashboardVisible,
      boolean updateStatusButtonVisible) {
    this.titleFields = titleFields;
    this.attributeFields = attributeFields;
    this.deleteButtonOnPatientDashboardVisible = deleteButtonOnPatientDashboardVisible;
    this.updateStatusButtonVisible = updateStatusButtonVisible;
  }

  public PatientHeaderConfigModel() {}

  public List<PatientHeaderFieldModel> getTitleFields() {
    return titleFields;
  }

  public void setTitleFields(List<PatientHeaderFieldModel> titleFields) {
    this.titleFields = titleFields;
  }

  public List<PatientHeaderFieldModel> getAttributeFields() {
    return attributeFields;
  }

  public void setAttributeFields(List<PatientHeaderFieldModel> attributeFields) {
    this.attributeFields = attributeFields;
  }

  public boolean isDeleteButtonOnPatientDashboardVisible() {
    return deleteButtonOnPatientDashboardVisible;
  }

  public void setDeleteButtonOnPatientDashboardVisible(
      boolean deleteButtonOnPatientDashboardVisible) {
    this.deleteButtonOnPatientDashboardVisible = deleteButtonOnPatientDashboardVisible;
  }

  public boolean isUpdateStatusButtonVisible() {
    return updateStatusButtonVisible;
  }

  public void setUpdateStatusButtonVisible(boolean updateStatusButtonVisible) {
    this.updateStatusButtonVisible = updateStatusButtonVisible;
  }
}
