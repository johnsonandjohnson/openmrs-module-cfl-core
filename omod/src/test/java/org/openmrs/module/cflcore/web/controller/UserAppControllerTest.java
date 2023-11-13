package org.openmrs.module.cflcore.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.test.BaseContextMockTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class UserAppControllerTest extends BaseContextMockTest {
  @Mock private AppFrameworkService appFrameworkServiceMock;

  @Before
  public void setup() {
    contextMockHelper.setService(AppFrameworkService.class, appFrameworkServiceMock);
  }

  @Test
  public void shouldReturnRegisterPatientAppOnly() {
    final AppDescriptor registerPatientApp = new AppDescriptor();
    registerPatientApp.setId("cflui.registerPatient");
    final AppDescriptor otherApp = new AppDescriptor();
    otherApp.setId("cflui.otherApp");

    when(appFrameworkServiceMock.getAllApps())
        .thenReturn(Arrays.asList(registerPatientApp, otherApp));

    final UserAppController controller = new UserAppController();
    final List<AppDescriptor> appDescriptors = controller.getSpecificUserApps().getBody();

    assertEquals(1, appDescriptors.size());
    assertEquals(registerPatientApp.getId(), appDescriptors.get(0).getId());
  }
}
