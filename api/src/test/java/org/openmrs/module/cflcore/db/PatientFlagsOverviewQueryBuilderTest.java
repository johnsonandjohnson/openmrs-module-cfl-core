package org.openmrs.module.cflcore.db;

import org.junit.Test;
import org.openmrs.module.cflcore.domain.criteria.PatientFlagsOverviewCriteria;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.evaluator.SQLFlagEvaluator;
import org.openmrs.test.BaseContextMockTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PatientFlagsOverviewQueryBuilderTest extends BaseContextMockTest {

  @Test
  public void shouldAddLocationToQuery() {
    final PatientFlagsOverviewCriteria criteria = new PatientFlagsOverviewCriteria();
    criteria.setLocationUuid("03831ad3-68b8-440f-af82-afbd62915f36");

    final PatientFlagsOverviewQueryBuilder builder = new PatientFlagsOverviewQueryBuilder();
    final String query = builder.buildQueryByCriteria(criteria);

    assertTrue(query.contains("WHERE pa1.value = :locationUuidParam"));
    assertTrue(builder.getQueryParams().containsKey("locationUuidParam"));
    assertEquals(criteria.getLocationUuid(), builder.getQueryParams().get("locationUuidParam"));
  }

  @Test
  public void shouldIgnoreAnyNonSQLFlag() {
    final String notSqlFlagName = "not-sql";
    final FlagService flagServiceMock = mock(FlagService.class);
    when(flagServiceMock.getFlagByName(notSqlFlagName)).thenReturn(new Flag());
    contextMockHelper.setService(FlagService.class, flagServiceMock);

    final PatientFlagsOverviewCriteria criteria = new PatientFlagsOverviewCriteria();
    final PatientFlagsOverviewCriteria criteriaWithNonSQLFlag = new PatientFlagsOverviewCriteria();
    criteriaWithNonSQLFlag.setFlagName(notSqlFlagName);

    final String queryWithoutFlag =
        new PatientFlagsOverviewQueryBuilder().buildQueryByCriteria(criteria);
    final String queryWithNonSQLFlag =
        new PatientFlagsOverviewQueryBuilder().buildQueryByCriteria(criteriaWithNonSQLFlag);

    assertEquals(queryWithoutFlag, queryWithNonSQLFlag);
  }

  @Test
  public void shouldIntegrateSQLFlagQuery() {
    final Flag flag = new Flag();
    flag.setName("myFlag");
    flag.setEvaluator(SQLFlagEvaluator.class.getName());
    flag.setCriteria("query:fc7deb71-cad2-43d6-b289-9cc1ef42330c");

    final FlagService flagServiceMock = mock(FlagService.class);
    when(flagServiceMock.getFlagByName(flag.getName())).thenReturn(flag);
    contextMockHelper.setService(FlagService.class, flagServiceMock);

    final PatientFlagsOverviewCriteria criteria = new PatientFlagsOverviewCriteria();
    criteria.setFlagName(flag.getName());

    final String query = new PatientFlagsOverviewQueryBuilder().buildQueryByCriteria(criteria);

    assertTrue(query.contains(flag.getCriteria()));
  }

  @Test
  public void shouldAddSearchRestrictions() {
    final String searchRestriction = "searchRestriction:6f1aed3b-c59b-42f3-9ad1-7c776cbe7823";
    final PatientFlagsOverviewCriteria criteria = new PatientFlagsOverviewCriteria();
    criteria.setQuery(searchRestriction);

    final PatientFlagsOverviewQueryBuilder builder = new PatientFlagsOverviewQueryBuilder();
    builder.buildQueryByCriteria(criteria);

    assertTrue(builder.getQueryParams().containsValue('%' + searchRestriction + '%'));
  }

  @Test
  public void shouldAddPatientStatusRestrictions() {
    final String patientStatus = "patientStatus:5fc11388-de1d-456e-9695-b01517d6ab6a";
    final PatientFlagsOverviewCriteria criteria = new PatientFlagsOverviewCriteria();
    criteria.setPatientStatus(patientStatus);

    final PatientFlagsOverviewQueryBuilder builder = new PatientFlagsOverviewQueryBuilder();
    builder.buildQueryByCriteria(criteria);

    assertTrue(builder.getQueryParams().containsValue(patientStatus));
  }

  @Test
  public void shouldContainOrderByClause() {
    final PatientFlagsOverviewCriteria criteria = new PatientFlagsOverviewCriteria();
    final PatientFlagsOverviewQueryBuilder builder = new PatientFlagsOverviewQueryBuilder();
    final String query = builder.buildQueryByCriteria(criteria);

    assertTrue(query.contains("ORDER by"));
  }
}
