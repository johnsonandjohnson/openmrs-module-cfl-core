package org.openmrs.module.cflcore.db;

import org.hibernate.SQLQuery;
import org.hibernate.type.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cflcore.api.dto.PatientFlagsOverviewDTO;
import org.openmrs.module.cflcore.db.impl.PatientFlagsOverviewDAOImpl;
import org.openmrs.module.cflcore.domain.criteria.PatientFlagsOverviewCriteria;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PatientFlagsOverviewDAOImplTest {
  @Mock private SQLQuery sqlQueryMock;

  @Mock private DbSession dbSessionMock;

  @Mock private DbSessionFactory dbSessionFactoryMock;

  @Before
  public void setup() {
    when(dbSessionFactoryMock.getCurrentSession()).thenReturn(dbSessionMock);
    when(dbSessionMock.createSQLQuery(anyString())).thenReturn(sqlQueryMock);
    when(sqlQueryMock.addScalar(anyString(), any(Type.class))).thenReturn(sqlQueryMock);
  }

  @Test
  public void countShouldBeDoneOnDBSide() {
    final PatientFlagsOverviewDAOImpl dao = new PatientFlagsOverviewDAOImpl();
    dao.setDbSessionFactory(dbSessionFactoryMock);

    final PatientFlagsOverviewCriteria criteria = new PatientFlagsOverviewCriteria();
    final Integer pageNumber = 0;
    final Integer pageSize = 20;

    dao.getPatientsWithFlag(criteria, pageNumber, pageSize);

    verify(sqlQueryMock).uniqueResult();
  }

  @Test
  public void shouldReturnPaginatedPatients() {
    final Object[] resultRow = new Object[] {"", "", "", "", "", ""};
    when(sqlQueryMock.list()).thenReturn(Arrays.asList(resultRow, resultRow));
    when(sqlQueryMock.uniqueResult()).thenReturn(4L);

    final PatientFlagsOverviewDAOImpl dao = new PatientFlagsOverviewDAOImpl();
    dao.setDbSessionFactory(dbSessionFactoryMock);

    final PatientFlagsOverviewCriteria criteria = new PatientFlagsOverviewCriteria();
    final Integer pageNumber = 0;
    final Integer pageSize = 2;

    PatientFlagsOverviewDTO dto = dao.getPatientsWithFlag(criteria, pageNumber, pageSize);

    assertEquals(2, dto.getFlaggedPatients().size());
  }
}
