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

public class PatientHeaderConfigDTO {

  private List<PatientHeaderFieldDTO> titleFieldDTOs;

  private List<PatientHeaderFieldDTO> attributeFieldDTOs;

  private boolean deleteButtonOnPatientDashboardVisible;

  private boolean updateStatusButtonVisible;

  public PatientHeaderConfigDTO(
      List<PatientHeaderFieldDTO> titleFieldDTOs,
      List<PatientHeaderFieldDTO> attributeFieldDTOs,
      boolean deleteButtonOnPatientDashboardVisible,
      boolean updateStatusButtonVisible) {
    this.titleFieldDTOs = titleFieldDTOs;
    this.attributeFieldDTOs = attributeFieldDTOs;
    this.deleteButtonOnPatientDashboardVisible = deleteButtonOnPatientDashboardVisible;
    this.updateStatusButtonVisible = updateStatusButtonVisible;
  }

  public List<PatientHeaderFieldDTO> getTitleFieldDTOs() {
    return titleFieldDTOs;
  }

  public void setTitleFieldDTOs(List<PatientHeaderFieldDTO> titleFieldDTOs) {
    this.titleFieldDTOs = titleFieldDTOs;
  }

  public List<PatientHeaderFieldDTO> getAttributeFieldDTOs() {
    return attributeFieldDTOs;
  }

  public void setAttributeFieldDTOs(List<PatientHeaderFieldDTO> attributeFieldDTOs) {
    this.attributeFieldDTOs = attributeFieldDTOs;
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
