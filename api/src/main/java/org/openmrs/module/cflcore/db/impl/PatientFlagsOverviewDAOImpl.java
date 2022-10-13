/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.db.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.SQLQuery;
import org.hibernate.type.LongType;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cflcore.api.dto.FlaggedPatientDTO;
import org.openmrs.module.cflcore.api.dto.PatientFlagsOverviewDTO;
import org.openmrs.module.cflcore.db.PatientFlagsOverviewDAO;
import org.openmrs.module.cflcore.db.PatientFlagsOverviewQueryBuilder;
import org.openmrs.module.cflcore.domain.criteria.PatientFlagsOverviewCriteria;

public class PatientFlagsOverviewDAOImpl implements PatientFlagsOverviewDAO {

  private DbSessionFactory dbSessionFactory;

  @Override
  public PatientFlagsOverviewDTO getPatientsWithFlag(PatientFlagsOverviewCriteria criteria,
      Integer pageNumber, Integer pageSize) {

    PatientFlagsOverviewQueryBuilder patientFlagsOverviewQueryBuilder = new PatientFlagsOverviewQueryBuilder();
    String queryToExecute = patientFlagsOverviewQueryBuilder.buildQueryByCriteria(criteria);
    Map<String, String> queryParams = patientFlagsOverviewQueryBuilder.getQueryParams();

    Long totalCount = getTotalCount(queryToExecute, queryParams);
    List<FlaggedPatientDTO> flaggedPatients = getFlaggedPatients(queryToExecute, queryParams,
        pageNumber, pageSize);

    return new PatientFlagsOverviewDTO(flaggedPatients, totalCount);
  }

  public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  private Long getTotalCount(String queryToExecute, Map<String, String> queryParams) {
    String totalCountQuery = "SELECT COUNT(*) FROM (" + queryToExecute + ") count";
    SQLQuery sqlQuery = dbSessionFactory.getCurrentSession().createSQLQuery(totalCountQuery)
        .addScalar("COUNT(*)",
            LongType.INSTANCE);
    setQueryParams(sqlQuery, queryParams);

    return (Long) sqlQuery.uniqueResult();
  }

  private List<FlaggedPatientDTO> getFlaggedPatients(String query, Map<String, String> queryParams,
      Integer pageNumber, Integer pageSize) {
    SQLQuery sqlQuery = getSQLQueryWithPaginationSet(query, pageNumber, pageSize);
    setQueryParams(sqlQuery, queryParams);

    List<Object[]> queryResult = sqlQuery.list();

    return queryResult.stream().map(FlaggedPatientDTO::new).collect(Collectors.toList());
  }

  private SQLQuery getSQLQueryWithPaginationSet(String query, Integer pageNumber,
      Integer pageSize) {
    SQLQuery sqlQuery = dbSessionFactory.getCurrentSession().createSQLQuery(query);

    if (pageNumber != null && pageSize != null) {
      sqlQuery.setFirstResult((pageNumber - 1) * pageSize);
      sqlQuery.setMaxResults(pageSize);
    }

    return sqlQuery;
  }

  private void setQueryParams(SQLQuery sqlQuery, Map<String, String> queryParams) {
    queryParams.forEach(sqlQuery::setParameter);
  }
}
