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
import org.openmrs.module.cflcore.api.htmlformentry.model.VaccinationDataModel;
import java.util.ArrayList;
import java.util.List;

public final class VaccinationUtil {

  public static final List<VaccinationDataModel> VACCINATION_DATA = new ArrayList<>();

  private static final String INFLUENZA_VACCINATION_CONCEPT_UUID =
      "116958AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  private static final String INFLUENZA_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "70fdbba0-578a-42b4-9670-04c2210b895c";
  private static final String INFLUENZA_VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "bd91eba8-3a59-4fc8-96f9-6e7f257d8877";
  private static final String PCV_13_VACCINATION_CONCEPT_UUID =
      "165575AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  private static final String PCV_13_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "57477d5f-eaca-4a02-b6e9-f229e523684c";
  private static final String PCV_13_VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "add9112a-87d9-4d74-a0ec-667452feaa78";
  private static final String HAV_VACCINATION_CONCEPT_UUID = "660af2e3-96db-4cb4-ac91-13644121a751";
  private static final String HAV_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "d2abdab2-99a0-4ce9-9709-f173264eea22";
  private static final String HAV_VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "730949a8-9714-431c-888d-f6ac9154dc26";
  private static final String MEN_ACWY_VACCINATION_CONCEPT_UUID =
      "acf76645-0ba4-4eca-a5ef-f951b40063e3";
  private static final String MEN_ACWY_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "ffadb22d-aea6-44e1-8cbc-f46a955e2f73";
  private static final String MEN_ACWY_VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "1e0c9845-c7bd-40f4-8b0f-b6b41c3512f7";
  private static final String VARICELLA_VACCINATION_CONCEPT_UUID =
      "892AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  private static final String VARICELLA_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "6f926421-1d9b-443e-a691-7dff1ea882dd";
  private static final String VARICELLA_VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "80b03c49-8578-4bcb-bcc8-a7494fb6ecae";
  private static final String COVID_19_VACCINATION_CONCEPT_UUID =
      "165621AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  private static final String COVID_19_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "be3bd67e-2527-45b5-9c0a-902e4577094d";
  private static final String COVID_19_VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "1e781715-1a99-4438-82a0-b38667341a27";
  private static final String PPSV_23_VACCINATION_CONCEPT_UUID =
      "9d3b4c50-f325-4236-bc5b-972ba1bc2192";
  private static final String PPSV_23_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "6a9edd84-8591-478b-9641-085b4de8c6b7";
  private static final String PPSV_23VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "2d77205a-20f1-493a-91a4-432085fa4730";
  private static final String HBV_VACCINATION_CONCEPT_UUID = "d7f9402c-1c44-4ad2-ad5c-9f71164b0d3e";
  private static final String HBV_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "9b61ba17-3665-469f-8f22-236ed16b0d9c";
  private static final String HBV_VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "a0098f5d-52a0-47b0-9622-2e4b74b8e99b";
  private static final String HPV_VACCINATION_CONCEPT_UUID = "1213AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  private static final String HPV_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "202100ce-3b7b-455c-bf72-912a62c8887e";
  private static final String HPV_VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "0c83afde-9655-4552-a6de-03a5540fba11";
  private static final String TDAP_VACCINATION_CONCEPT_UUID =
      "56d002fe-dddb-4a73-8807-ff51d9d602a8";
  private static final String TDAP_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "6d39af4b-c1ac-4439-b5c5-534a30d6e758";
  private static final String TDAP_VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "84b6f9ad-e361-4c82-a8cd-928527264eaf";
  private static final String MMR_VACCINATION_CONCEPT_UUID = "6dd8c2eb-1470-4a46-9ea1-142ff2c415c2";
  private static final String MMR_VACCINATION_DATE_RECEIVED_CONCEPT_UUID =
      "4c89b40b-70e1-49fc-b4d9-6fd10bf2c6a1";
  private static final String MMR_VACCINATION_LOT_NUMBER_CONCEPT_UUID =
      "6997d943-7555-4bb7-b41d-89f996167d6c";

  static {
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            INFLUENZA_VACCINATION_CONCEPT_UUID,
            INFLUENZA_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            INFLUENZA_VACCINATION_LOT_NUMBER_CONCEPT_UUID));
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            PCV_13_VACCINATION_CONCEPT_UUID,
            PCV_13_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            PCV_13_VACCINATION_LOT_NUMBER_CONCEPT_UUID));
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            HAV_VACCINATION_CONCEPT_UUID,
            HAV_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            HAV_VACCINATION_LOT_NUMBER_CONCEPT_UUID));
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            MEN_ACWY_VACCINATION_CONCEPT_UUID,
            MEN_ACWY_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            MEN_ACWY_VACCINATION_LOT_NUMBER_CONCEPT_UUID));
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            VARICELLA_VACCINATION_CONCEPT_UUID,
            VARICELLA_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            VARICELLA_VACCINATION_LOT_NUMBER_CONCEPT_UUID));
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            COVID_19_VACCINATION_CONCEPT_UUID,
            COVID_19_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            COVID_19_VACCINATION_LOT_NUMBER_CONCEPT_UUID));
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            PPSV_23_VACCINATION_CONCEPT_UUID,
            PPSV_23_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            PPSV_23VACCINATION_LOT_NUMBER_CONCEPT_UUID));
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            HBV_VACCINATION_CONCEPT_UUID,
            HBV_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            HBV_VACCINATION_LOT_NUMBER_CONCEPT_UUID));
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            HPV_VACCINATION_CONCEPT_UUID,
            HPV_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            HPV_VACCINATION_LOT_NUMBER_CONCEPT_UUID));
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            TDAP_VACCINATION_CONCEPT_UUID,
            TDAP_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            TDAP_VACCINATION_LOT_NUMBER_CONCEPT_UUID));
    VACCINATION_DATA.add(
        new VaccinationDataModel(
            MMR_VACCINATION_CONCEPT_UUID,
            MMR_VACCINATION_DATE_RECEIVED_CONCEPT_UUID,
            MMR_VACCINATION_LOT_NUMBER_CONCEPT_UUID));
  }

  public static VaccinationDataModel findVaccinationDataModelByVaccinationUuid(
      String vaccinationUuid) {
    return VACCINATION_DATA.stream()
        .filter(
            vaccinationDataModel ->
                StringUtils.equals(
                    vaccinationDataModel.getVaccinationConceptUuid(), vaccinationUuid))
        .findFirst()
        .orElse(null);
  }

  private VaccinationUtil() {}
}
