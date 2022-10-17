/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.db;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.domain.criteria.PatientFlagsOverviewCriteria;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.evaluator.SQLFlagEvaluator;

public class PatientFlagsOverviewQueryBuilder {

  public static final Integer PATIENT_IDENTIFIER_RESULT_INDEX = 0;

  public static final Integer PATIENT_NAME_RESULT_INDEX = 1;

  public static final Integer PHONE_NUMBER_RESULT_INDEX = 2;

  public static final Integer PATIENT_STATUS_RESULT_INDEX = 3;

  public static final Integer PATIENT_UUID_RESULT_INDEX = 4;

  private static final String BASIC_QUERY =
      "SELECT pi.identifier AS identifier, concat_ws(' ', pn.given_name, pn.middle_name, pn.family_name) AS patientName, pa2.value AS phoneNumber, pa3.value AS patientStatus, per.uuid AS patientUuid "
          + "FROM patient p "
          + "INNER JOIN person per on p.patient_id = per.person_id "
          + "LEFT JOIN person_attribute pa1 ON p.patient_id = pa1.person_id "
          + "AND pa1.person_attribute_type_id = "
          + "(select person_attribute_type_id from person_attribute_type where name = (select property_value from global_property where property = '"
          + CFLConstants.PERSON_LOCATION_ATTRIBUTE_KEY + "')) AND pa1.voided = 0 "
          + "LEFT JOIN person_attribute pa2 ON p.patient_id = pa2.person_id "
          + "AND pa2.person_attribute_type_id = "
          + "(select person_attribute_type_id from person_attribute_type where name = '"
          + CFLConstants.TELEPHONE_ATTRIBUTE_NAME + "') AND pa2.voided = 0 "
          + "LEFT JOIN person_attribute pa3 ON p.patient_id = pa3.person_id "
          + "AND pa3.person_attribute_type_id = "
          + "(select person_attribute_type_id from person_attribute_type where name = '"
          + CFLConstants.PERSON_STATUS_ATTRIBUTE_TYPE_NAME + "') AND pa3.voided = 0 "
          + "INNER JOIN patient_identifier pi ON pi.patient_id = p.patient_id "
          + "AND pi.identifier_type = "
          + "(select patient_identifier_type_id from patient_identifier_type where name = (select property_value from global_property where property = '"
          + CFLConstants.PATIENT_FLAGS_OVERVIEW_IDENTIFIER_TYPE_FOR_SEARCH_GP_KEY
          + "')) AND pi.voided = 0 "
          + "INNER JOIN person_name pn ON pn.person_id = p.patient_id AND pn.voided = 0 ";

  private final StringBuilder queryBuilder;

  private final Map<String, String> queryParams;

  public PatientFlagsOverviewQueryBuilder() {
    this.queryBuilder = new StringBuilder(BASIC_QUERY);
    this.queryParams = new HashMap<>();
  }

  public StringBuilder getQueryBuilder() {
    return queryBuilder;
  }

  public Map<String, String> getQueryParams() {
    return queryParams;
  }

  public String buildQueryByCriteria(PatientFlagsOverviewCriteria criteria) {
    addLocationRestriction(criteria.getLocationUuid());
    addFlagRestriction(criteria.getFlagName());
    addSearchRestrictions(criteria.getQuery());
    addPatientStatusRestriction(criteria.getPatientStatus());
    addSortingByName();

    return queryBuilder.toString();
  }

  private void addLocationRestriction(String locationUuid) {
    if (StringUtils.isNotBlank(locationUuid)) {
      String queryPart = "WHERE pa1.value = :locationUuidParam ";
      queryBuilder.append(queryPart);
      queryParams.put("locationUuidParam", locationUuid);
    }
  }

  private void addFlagRestriction(String flagName) {
    if (StringUtils.isNotBlank(flagName)) {
      Flag flag = Context.getService(FlagService.class).getFlagByName(flagName);
      if (isFlagSQLType(flag)) {
        String flagQuery = getFlagQuery(flag);
        String queryPart =
            "AND p.patient_id IN (select patient_id FROM (" + flagQuery + ") flagQueryIDs) ";
        queryBuilder.append(queryPart);
      }
    }
  }

  private String getFlagQuery(Flag flag) {
    String flagQuery = flag.getCriteria();
    if (flagQuery.endsWith(";")) {
      flagQuery = StringUtils.chop(flagQuery);
    }

    return flagQuery;
  }

  private boolean isFlagSQLType(Flag flag) {
    return StringUtils.equals(flag.getEvaluator(), SQLFlagEvaluator.class.getName());
  }

  private void addSearchRestrictions(String query) {
    if (StringUtils.isNotBlank(query)) {
      String queryPart = "AND (pa2.value like :queryParam "
          + "OR pi.identifier like :queryParam "
          + "OR concat_ws(' ', pn.given_name, pn.middle_name, pn.family_name) like :queryParam )";
      queryBuilder.append(queryPart);
      queryParams.put("queryParam", "%" + query + "%");
    }
  }

  private void addPatientStatusRestriction(String patientStatus) {
    if (StringUtils.isNotBlank(patientStatus)) {
      String queryPart = "AND pa3.value = :patientStatusParam ";
      queryBuilder.append(queryPart);
      queryParams.put("patientStatusParam", patientStatus);
    }
  }

  private void addSortingByName() {
    String queryPart = "ORDER by pn.family_name, pn.given_name";
    queryBuilder.append(queryPart);
  }
}
