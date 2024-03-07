/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.util;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.cflcore.api.htmlformentry.model.PhysicalExaminationDataModel;
import java.util.ArrayList;
import java.util.List;

public final class PhysicalExaminationUtil {

  public static final List<PhysicalExaminationDataModel> PHYSICAL_EXAMINATION_DATA =
      new ArrayList<>();

  private static final String GENERAL_PE_NAME_CONCEPT_UUID = "7861918c-48c7-4702-b927-7dc1a0beceea";

  private static final String GENERAL_PE_RESULT_CONCEPT_UUID =
      "7d13f632-d94c-4114-ba94-b4ba51e25f20";

  private static final String GENERAL_PE_DETAILS_CONCEPT_UUID =
      "c6f81c09-c915-4f6b-9d38-da1adae93848";

  private static final String CARDIOVASCULAR_PE_NAME_CONCEPT_UUID =
      "eb19e008-e352-4f54-8567-8f57bbc3d164";

  private static final String CARDIOVASCULAR_PE_RESULT_CONCEPT_UUID =
      "74f3db8e-c71e-4f3e-ad99-2124241e09d3";

  private static final String CARDIOVASCULAR_PE_DETAILS_CONCEPT_UUID =
      "51cb2255-88d9-4fbd-8913-7e138680007e";

  private static final String GASTROINTESTINAL_PE_NAME_CONCEPT_UUID =
      "085082da-f3e4-4b95-a4fd-501f712d3433";

  private static final String GASTROINTESTINAL_PE_RESULT_CONCEPT_UUID =
      "166663AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

  private static final String GASTROINTESTINAL_PE_DETAILS_CONCEPT_UUID =
      "da538586-4138-4273-ac3e-f7eb08713e6b";

  private static final String REPRODUCTIVE_PE_NAME_CONCEPT_UUID =
      "ed1dd86f-f4d1-4835-b156-6cd4c5c0e0b9";

  private static final String REPRODUCTIVE_PE_RESULT_CONCEPT_UUID =
      "166665AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

  private static final String REPRODUCTIVE_PE_DETAILS_CONCEPT_UUID =
      "c3ae6f8b-e759-4b4f-a36c-50a065ee763e";

  private static final String NEUROLOGY_PE_NAME_CONCEPT_UUID =
      "165023AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

  private static final String NEUROLOGY_PE_RESULT_CONCEPT_UUID =
      "d88e8674-d5c9-4d54-bad4-fc65d654f6a2";

  private static final String NEUROLOGY_PE_DETAILS_CONCEPT_UUID =
      "85246a3b-9073-4d1f-a455-24792cf85e01";

  private static final String HEMATOLOGY_PE_ONCOLOGY_PE_NAME_CONCEPT_UUID =
      "1a4b6127-25bc-4b12-adc2-509b61f8f670";

  private static final String HEMATOLOGY_PE_ONCOLOGY_RESULT_CONCEPT_UUID =
      "cf319524-4f91-443c-a5fb-d11217132ee3";

  private static final String HEMATOLOGY_PE_ONCOLOGY_DETAILS_CONCEPT_UUID =
      "2791ff41-651f-41ea-98a7-5ec1621871b4";

  private static final String HEENT_PE_NAME_CONCEPT_UUID = "91325071-c770-49d7-a6e1-26a2f4455cc2";

  private static final String HEENT_PE_RESULT_CONCEPT_UUID = "7d8e04e6-f194-4ebe-8c68-edeb74277149";

  private static final String HEENT_PE_DETAILS_CONCEPT_UUID =
      "61759abc-e476-4508-8422-fb7a2f4f8cc3";

  private static final String RESPIRATORY_PE_NAME_CONCEPT_UUID =
      "0af312b8-2806-4252-bca4-7968f64de28c";

  private static final String RESPIRATORY_PE_RESULT_CONCEPT_UUID =
      "166662AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

  private static final String RESPIRATORY_PE_DETAILS_CONCEPT_UUID =
      "88128239-e71e-450f-bb63-050f9c1a54dd";

  private static final String GENITOURINARY_PE_NAME_CONCEPT_UUID =
      "78e5daa6-0393-4e5f-a047-83f7c6581300";

  private static final String GENITOURINARY_PE_RESULT_CONCEPT_UUID =
      "e21034c3-3fa7-4d87-82da-b730bfc0156c";

  private static final String GENITOURINARY_PE_DETAILS_CONCEPT_UUID =
      "e2947a46-ce83-41ac-8fb5-498f1c88c76d";

  private static final String MUSCULOSKELETAL_PE_NAME_CONCEPT_UUID =
      "5297AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

  private static final String MUSCULOSKELETAL_PE_RESULT_CONCEPT_UUID =
      "3fdda0a1-8c50-43d2-99ed-7dadca8cd40b";

  private static final String MUSCULOSKELETAL_PE_DETAILS_CONCEPT_UUID =
      "94dbc638-433d-4fa2-9170-d3f58b51ab78";

  private static final String DERMATOLOGY_PE_NAME_CONCEPT_UUID =
      "165003AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

  private static final String DERMATOLOGY_PE_RESULT_CONCEPT_UUID =
      "43def4aa-f541-4e42-94e8-e3ab78f93e8e";

  private static final String DERMATOLOGY_PE_DETAILS_CONCEPT_UUID =
      "a3339fb1-e0a7-45b4-90f9-bf320446dae5";

  static {
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            GENERAL_PE_NAME_CONCEPT_UUID,
            GENERAL_PE_RESULT_CONCEPT_UUID,
            GENERAL_PE_DETAILS_CONCEPT_UUID));
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            CARDIOVASCULAR_PE_NAME_CONCEPT_UUID,
            CARDIOVASCULAR_PE_RESULT_CONCEPT_UUID,
            CARDIOVASCULAR_PE_DETAILS_CONCEPT_UUID));
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            GASTROINTESTINAL_PE_NAME_CONCEPT_UUID,
            GASTROINTESTINAL_PE_RESULT_CONCEPT_UUID,
            GASTROINTESTINAL_PE_DETAILS_CONCEPT_UUID));
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            REPRODUCTIVE_PE_NAME_CONCEPT_UUID,
            REPRODUCTIVE_PE_RESULT_CONCEPT_UUID,
            REPRODUCTIVE_PE_DETAILS_CONCEPT_UUID));
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            NEUROLOGY_PE_NAME_CONCEPT_UUID,
            NEUROLOGY_PE_RESULT_CONCEPT_UUID,
            NEUROLOGY_PE_DETAILS_CONCEPT_UUID));
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            HEMATOLOGY_PE_ONCOLOGY_PE_NAME_CONCEPT_UUID,
            HEMATOLOGY_PE_ONCOLOGY_RESULT_CONCEPT_UUID,
            HEMATOLOGY_PE_ONCOLOGY_DETAILS_CONCEPT_UUID));
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            HEENT_PE_NAME_CONCEPT_UUID,
            HEENT_PE_RESULT_CONCEPT_UUID,
            HEENT_PE_DETAILS_CONCEPT_UUID));
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            RESPIRATORY_PE_NAME_CONCEPT_UUID,
            RESPIRATORY_PE_RESULT_CONCEPT_UUID,
            RESPIRATORY_PE_DETAILS_CONCEPT_UUID));
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            GENITOURINARY_PE_NAME_CONCEPT_UUID,
            GENITOURINARY_PE_RESULT_CONCEPT_UUID,
            GENITOURINARY_PE_DETAILS_CONCEPT_UUID));
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            MUSCULOSKELETAL_PE_NAME_CONCEPT_UUID,
            MUSCULOSKELETAL_PE_RESULT_CONCEPT_UUID,
            MUSCULOSKELETAL_PE_DETAILS_CONCEPT_UUID));
    PHYSICAL_EXAMINATION_DATA.add(
        new PhysicalExaminationDataModel(
            DERMATOLOGY_PE_NAME_CONCEPT_UUID,
            DERMATOLOGY_PE_RESULT_CONCEPT_UUID,
            DERMATOLOGY_PE_DETAILS_CONCEPT_UUID));
  }

  public static PhysicalExaminationDataModel findPhysicalExaminationDataModelByPENameConceptUuid(
      String peNameConceptUuid) {
    return PHYSICAL_EXAMINATION_DATA.stream()
        .filter(
            physicalExaminationDataModel ->
                StringUtils.equals(
                    physicalExaminationDataModel.getNameConceptUuid(), peNameConceptUuid))
        .findFirst()
        .orElse(null);
  }

  private PhysicalExaminationUtil() {}
}
