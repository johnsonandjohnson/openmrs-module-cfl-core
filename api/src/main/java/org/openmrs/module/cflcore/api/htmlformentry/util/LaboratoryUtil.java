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
import org.openmrs.module.cflcore.api.htmlformentry.model.LaboratoryDataModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaboratoryUtil {

  public static final List<LaboratoryDataModel> LABORATORY_DATA = new ArrayList<>();

  public static final List<String> CONCEPT_DATE_DONE_UUID_LIST;

  private static final String HEMOGLOBIN_DATE_DONE_CONCEPT_UUID =
      "003c56b8-70bb-4ffa-9052-b17ffe0b3ebf";
  private static final String HEMOGLOBIN_RESULTS_CONCEPT_UUID =
      "83e3b202-50cc-4c5f-beb7-7737330c9846";
  private static final String VIRAL_LOAD_DATE_DONE_CONCEPT_UUID =
      "5e1c0359-45dd-4940-8412-9b88db0fdb26";
  private static final String VIRAL_LOAD_SYMBOL_CONCEPT_UUID =
      "8d86c0c7-94a6-4058-83d3-e9a487716e86";
  private static final String VIRAL_LOAD_RESULTS_CONCEPT_UUID =
      "9b2b2c6b-bc10-44d8-85ad-30845c2450d4";
  private static final String CD4_DATE_DONE_CONCEPT_UUID = "4db35ffa-b148-4aea-92fb-5101ee1f8630";
  private static final String CD4_RESULTS_CONCEPT_UUID = "5afb5247-d139-486c-a586-6c170a1cfd62";
  private static final String SERUM_CREATININE_DATE_DONE_CONCEPT_UUID =
      "6f0c1647-19f4-4e94-8301-3100c4079223";
  private static final String SERUM_CREATININE_RESULTS_CONCEPT_UUID =
      "165836AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  private static final String SERUM_CREATININE_CLEARANCE_CONCEPT_UUID =
      "165839AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  private static final String HBSAG_DATE_DONE_CONCEPT_UUID = "7847ec58-4e03-405d-a3f3-6779f739215a";
  private static final String HBSAG_RESULTS_CONCEPT_UUID = "75ccce27-ce44-459f-9717-2267bf7b6cdf";
  private static final String CHEST_X_RAY_DATE_DONE_CONCEPT_UUID =
      "33b72f89-419b-446c-bf7b-f731aba0c144";
  private static final String CHEST_X_RAY_RESULTS_CONCEPT_UUID =
      "e921cfda-9b62-41b6-a711-f666e2df8d71";
  private static final String GENE_XPERT_DATE_DONE_CONCEPT_UUID =
      "9afb75af-384c-4684-ac41-734cd8d1a82a";
  private static final String GENE_XPERT_RESULTS_CONCEPT_UUID =
      "2e53265d-0825-435a-b61f-7f39af4f5e47";
  private static final String DSSM_DST_DATE_DONE_CONCEPT_UUID =
      "9c255270-50c4-4186-adb0-ef0696c23347";
  private static final String DSSM_DST_RESULTS_CONCEPT_UUID =
      "e02e5e00-b0d0-40b3-a325-623169932f57";
  private static final String HIVDR_GENOTYPE_DATE_DONE_CONCEPT_UUID =
      "930dfaae-ab10-4a69-92b7-f2e243ee2776";
  private static final String HIVDR_GENOTYPE_RESULTS_CONCEPT_UUID =
      "5ab801aa-d0a5-49cf-8d79-0147d2eb6aeb";

  static {
    LABORATORY_DATA.add(
        new LaboratoryDataModel(
            "Hemoglobin",
            HEMOGLOBIN_DATE_DONE_CONCEPT_UUID,
            HEMOGLOBIN_RESULTS_CONCEPT_UUID,
            "g/dL",
            null,
            null));
    LABORATORY_DATA.add(
        new LaboratoryDataModel(
            "Viral Load",
            VIRAL_LOAD_DATE_DONE_CONCEPT_UUID,
            VIRAL_LOAD_RESULTS_CONCEPT_UUID,
            "copies/mL",
            VIRAL_LOAD_SYMBOL_CONCEPT_UUID,
            null));
    LABORATORY_DATA.add(
        new LaboratoryDataModel(
            "CD4", CD4_DATE_DONE_CONCEPT_UUID, CD4_RESULTS_CONCEPT_UUID, "cells/µL", null, null));
    LABORATORY_DATA.add(
        new LaboratoryDataModel(
            "Serum creatinine",
            SERUM_CREATININE_DATE_DONE_CONCEPT_UUID,
            SERUM_CREATININE_RESULTS_CONCEPT_UUID,
            "mg/dL",
            null,
            SERUM_CREATININE_CLEARANCE_CONCEPT_UUID));
    LABORATORY_DATA.add(
        new LaboratoryDataModel(
            "HBsAg", HBSAG_DATE_DONE_CONCEPT_UUID, HBSAG_RESULTS_CONCEPT_UUID, null, null, null));
    LABORATORY_DATA.add(
        new LaboratoryDataModel(
            "Chest X-Ray",
            CHEST_X_RAY_DATE_DONE_CONCEPT_UUID,
            CHEST_X_RAY_RESULTS_CONCEPT_UUID,
            null,
            null,
            null));
    LABORATORY_DATA.add(
        new LaboratoryDataModel(
            "Gene Xpert",
            GENE_XPERT_DATE_DONE_CONCEPT_UUID,
            GENE_XPERT_RESULTS_CONCEPT_UUID,
            null,
            null,
            null));
    LABORATORY_DATA.add(
        new LaboratoryDataModel(
            "DSSM/DST",
            DSSM_DST_DATE_DONE_CONCEPT_UUID,
            DSSM_DST_RESULTS_CONCEPT_UUID,
            null,
            null,
            null));
    LABORATORY_DATA.add(
        new LaboratoryDataModel(
            "HIVDR and Genotype",
            HIVDR_GENOTYPE_DATE_DONE_CONCEPT_UUID,
            HIVDR_GENOTYPE_RESULTS_CONCEPT_UUID,
            null,
            null,
            null));

    CONCEPT_DATE_DONE_UUID_LIST =
        Arrays.asList(
            HEMOGLOBIN_DATE_DONE_CONCEPT_UUID,
            VIRAL_LOAD_DATE_DONE_CONCEPT_UUID,
            CD4_DATE_DONE_CONCEPT_UUID,
            SERUM_CREATININE_DATE_DONE_CONCEPT_UUID,
            HBSAG_DATE_DONE_CONCEPT_UUID,
            CHEST_X_RAY_DATE_DONE_CONCEPT_UUID,
            GENE_XPERT_DATE_DONE_CONCEPT_UUID,
            DSSM_DST_DATE_DONE_CONCEPT_UUID,
            HIVDR_GENOTYPE_DATE_DONE_CONCEPT_UUID);
  }

  public static LaboratoryDataModel findLaboratoryDataModelByDateDoneConceptUuid(
      String dateDoneConceptUuid) {
    return LABORATORY_DATA.stream()
        .filter(
            labData -> StringUtils.equals(labData.getDateDoneConceptUuid(), dateDoneConceptUuid))
        .findFirst()
        .orElse(null);
  }

  private LaboratoryUtil() {}
}
