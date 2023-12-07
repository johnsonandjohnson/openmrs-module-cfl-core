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

import java.util.Arrays;
import java.util.List;

public class VitalDetailsDTO {

  public static final String HEIGHT_CONCEPT_UUID = "5090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  public static final String WEIGHT_CONCEPT_UUID = "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  public static final String TEMPERATURE_CONCEPT_UUID = "5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  public static final String PULSE_CONCEPT_UUID = "5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  public static final String RESPIRATORY_RATE_CONCEPT_UUID = "5242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  public static final String BLOOD_PRESSURE_SYSTOLIC_CONCEPT_UUID =
      "5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  public static final String BLOOD_PRESSURE_DIASTOLIC_CONCEPT_UUID =
      "5086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  public static final String BLOOD_OXYGEN_SATURATION_CONCEPT_UUID =
      "5092AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  public static final List<String> VITALS_HISTORY_CONCEPT_UUID_LIST =
      Arrays.asList(
          HEIGHT_CONCEPT_UUID,
          WEIGHT_CONCEPT_UUID,
          TEMPERATURE_CONCEPT_UUID,
          PULSE_CONCEPT_UUID,
          RESPIRATORY_RATE_CONCEPT_UUID,
          BLOOD_PRESSURE_SYSTOLIC_CONCEPT_UUID,
          BLOOD_PRESSURE_DIASTOLIC_CONCEPT_UUID,
          BLOOD_OXYGEN_SATURATION_CONCEPT_UUID);

  private String conceptUuid;

  private String value;

  public VitalDetailsDTO(String conceptUuid, String value) {
    this.conceptUuid = conceptUuid;
    this.value = value;
  }

  public String getConceptUuid() {
    return conceptUuid;
  }

  public void setConceptUuid(String conceptUuid) {
    this.conceptUuid = conceptUuid;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
