package org.openmrs.module.cflcore.api.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.dto.PatientFlagsOverviewDTO;
import org.openmrs.module.cflcore.api.service.PatientFlagsOverviewService;
import org.openmrs.module.cflcore.domain.criteria.PatientFlagsOverviewCriteria;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.transaction.annotation.Transactional;

public class PatientFlagsOverviewServiceImpl implements PatientFlagsOverviewService {

  private DbSessionFactory dbSessionFactory;

  @Override
  @Transactional(readOnly = true)
  public List<PatientFlagsOverviewDTO> getPatientsWithFlag(PatientFlagsOverviewCriteria criteria,
      Integer pageNumber, Integer pageSize) {

    String queryToExecute = buildQueryToExecute(criteria);
    SQLQuery sqlQuery = getSQLQuery(queryToExecute, pageNumber, pageSize);
    List<Object[]> queryResult = sqlQuery.list();

    return queryResult.stream().map(PatientFlagsOverviewDTO::new).collect(Collectors.toList());
  }

  public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  private String buildQueryToExecute(PatientFlagsOverviewCriteria criteria) {
    String basicQuery =
        "SELECT pi.identifier AS identifier, concat_ws(' ', pn.given_name, pn.middle_name, pn.family_name) AS patientName, pa2.value AS phoneNumber, pa3.value AS patientStatus, per.uuid AS patientUuid "
            + "FROM patient p "
            + "INNER JOIN person per on p.patient_id = per.person_id "
            + "LEFT JOIN person_attribute pa1 ON p.patient_id = pa1.person_id "
            + "AND pa1.person_attribute_type_id = "
            + "(select person_attribute_type_id from person_attribute_type where name = (select property_value from global_property where property = '"
            + CFLConstants.PERSON_LOCATION_ATTRIBUTE_KEY + "')) "
            + "LEFT JOIN person_attribute pa2 ON p.patient_id = pa2.person_id "
            + "AND pa2.person_attribute_type_id = "
            + "(select person_attribute_type_id from person_attribute_type where name = '"
            + CFLConstants.TELEPHONE_ATTRIBUTE_NAME + "') "
            + "LEFT JOIN person_attribute pa3 ON p.patient_id = pa3.person_id "
            + "AND pa3.person_attribute_type_id = "
            + "(select person_attribute_type_id from person_attribute_type where name = '"
            + CFLConstants.PERSON_STATUS_ATTRIBUTE_TYPE_NAME + "') "
            + "INNER JOIN patient_identifier pi ON pi.patient_id = p.patient_id "
            + "AND pi.identifier_type = "
            + "(select patient_identifier_type_id from patient_identifier_type where name = '"
            + CFLConstants.OPENMRS_ID_PATIENT_IDENTIFIER_TYPE_NAME + "') "
            + "INNER JOIN person_name pn ON pn.person_id = p.patient_id "
            + "WHERE pa1.value = '" + criteria.getLocationUuid() + "' ";

    StringBuilder queryBuilder = new StringBuilder(basicQuery);
    addFlagRestriction(queryBuilder, criteria.getFlagName());
    addPhoneNumberRestriction(queryBuilder, criteria.getPhoneNumber());
    addPatientStatusRestriction(queryBuilder, criteria.getPatientStatus());
    addPatientIdentifier(queryBuilder, criteria.getPatientIdentifier());
    addPatientNameRestriction(queryBuilder, criteria.getPatientName());
    addSort(queryBuilder);

    return queryBuilder.toString();
  }

  private void addFlagRestriction(StringBuilder queryBuilder, String flagName) {
    if (StringUtils.isNotBlank(flagName)) {
      Flag flag = Context.getService(FlagService.class).getFlagByName(flagName);
      String flagQuery = flag.getCriteria();
      if (flagQuery.endsWith(";")) {
        flagQuery = StringUtils.chop(flagQuery);
      }

      String queryPart =
          "AND p.patient_id IN (select patient_id FROM (" + flagQuery + ") flagQueryIDs) ";
      queryBuilder.append(queryPart);
    }
  }

  private void addPhoneNumberRestriction(StringBuilder queryBuilder, String phoneNumber) {
    if (StringUtils.isNotBlank(phoneNumber)) {
      String queryPart = "AND pa2.value like '%" + phoneNumber + "%' ";
      queryBuilder.append(queryPart);
    }
  }

  private void addPatientStatusRestriction(StringBuilder queryBuilder, String patientStatus) {
    if (StringUtils.isNotBlank(patientStatus)) {
      String queryPart = "AND pa3.value = '" + patientStatus + "' ";
      queryBuilder.append(queryPart);
    }
  }

  private void addPatientIdentifier(StringBuilder queryBuilder, String patientIdentifier) {
    if (StringUtils.isNotBlank(patientIdentifier)) {
      String queryPart = "AND pi.identifier like '%" + patientIdentifier + "%' ";
      queryBuilder.append(queryPart);
    }
  }

  private void addPatientNameRestriction(StringBuilder queryBuilder, String patientName) {
    if (StringUtils.isNotBlank(patientName)) {
      String queryPart =
          "AND concat_ws(' ', pn.given_name, pn.middle_name, pn.family_name) like '%" + patientName
              + "%' ";
      queryBuilder.append(queryPart);
    }
  }

  private void addSort(StringBuilder queryBuilder) {
    String queryPart = "ORDER by pn.family_name, pn.given_name";
    queryBuilder.append(queryPart);
  }

  private SQLQuery getSQLQuery(String query, Integer pageNumber, Integer pageSize) {
    SQLQuery sqlQuery = dbSessionFactory.getCurrentSession().createSQLQuery(query);

    if (pageNumber != null && pageSize != null) {
      sqlQuery.setFirstResult(pageNumber - 1);
      sqlQuery.setMaxResults(pageSize);
    }

    return sqlQuery;
  }
}
