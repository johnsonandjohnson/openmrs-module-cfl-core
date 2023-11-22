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

public class PhysicalExaminationDataModel {

  private String nameConceptUuid;

  private String resultConceptUuid;

  private String detailsConceptUuid;

  public PhysicalExaminationDataModel(
      String nameConceptUuid, String resultConceptUuid, String detailsConceptUuid) {
    this.nameConceptUuid = nameConceptUuid;
    this.resultConceptUuid = resultConceptUuid;
    this.detailsConceptUuid = detailsConceptUuid;
  }

  public String getNameConceptUuid() {
    return nameConceptUuid;
  }

  public void setNameConceptUuid(String nameConceptUuid) {
    this.nameConceptUuid = nameConceptUuid;
  }

  public String getResultConceptUuid() {
    return resultConceptUuid;
  }

  public void setResultConceptUuid(String resultConceptUuid) {
    this.resultConceptUuid = resultConceptUuid;
  }

  public String getDetailsConceptUuid() {
    return detailsConceptUuid;
  }

  public void setDetailsConceptUuid(String detailsConceptUuid) {
    this.detailsConceptUuid = detailsConceptUuid;
  }
}
