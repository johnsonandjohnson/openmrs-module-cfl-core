package org.openmrs.module.cflcore.web.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.cflcore.api.service.PatientFlagsOverviewService;
import org.openmrs.module.cflcore.domain.criteria.PatientFlagsOverviewCriteria;
import org.openmrs.test.BaseContextMockTest;

import javax.persistence.EntityNotFoundException;

public class PatientFlagsOverviewControllerTest extends BaseContextMockTest {

  @Mock private LocationService locationServiceMock;
  @Mock private PatientFlagsOverviewService patientFlagsOverviewServiceMock;

  @Before
  public void setup() {
    contextMockHelper.setLocationService(locationServiceMock);
    contextMockHelper.setService(
        PatientFlagsOverviewService.class, patientFlagsOverviewServiceMock);
  }

  @Test(expected = EntityNotFoundException.class)
  public void shouldFailIfLocationIsNotFound() {
    final String unknownLocationUuid = "87abcabb-0ea7-499c-93ab-46bc2b509da6";

    final PatientFlagsOverviewController controller = new PatientFlagsOverviewController();
    controller.getPatientsByCriteria(unknownLocationUuid, null, null, null, null, null);
  }

  @Test
  public void shouldDelegateToPatientFlagsOverviewService() {
    final String locationUuid = "542c0f31-d67c-4919-8669-de178686950c";
    final String flagName = "MyFlag";
    final String myQuery = "one";
    final String patientStatus = "ACTIVE";
    final Integer pageNumber = 0;
    final Integer pageSize = 20;

    Mockito.when(locationServiceMock.getLocationByUuid(locationUuid)).thenReturn(new Location());

    final PatientFlagsOverviewController controller = new PatientFlagsOverviewController();
    controller.getPatientsByCriteria(
        locationUuid, flagName, myQuery, patientStatus, pageNumber, pageSize);

    ArgumentCaptor<PatientFlagsOverviewCriteria> criteriaCaptor =
        ArgumentCaptor.forClass(PatientFlagsOverviewCriteria.class);
    ArgumentCaptor<Integer> pageNumberCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> pageSizeCaptor = ArgumentCaptor.forClass(Integer.class);

    Mockito.verify(patientFlagsOverviewServiceMock)
        .getPatientsWithFlag(
            criteriaCaptor.capture(), pageNumberCaptor.capture(), pageSizeCaptor.capture());

    Assert.assertEquals(locationUuid, criteriaCaptor.getValue().getLocationUuid());
    Assert.assertEquals(pageNumber, pageNumberCaptor.getValue());
    Assert.assertEquals(pageSize, pageSizeCaptor.getValue());
  }
}
