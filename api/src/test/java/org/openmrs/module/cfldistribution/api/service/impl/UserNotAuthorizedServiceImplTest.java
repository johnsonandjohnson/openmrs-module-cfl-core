package org.openmrs.module.cfldistribution.api.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.module.cfldistribution.api.service.UserNotAuthorizedService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserNotAuthorizedServiceImplTest extends BaseModuleContextSensitiveTest {

  private static final String USER_DATA_SET_FILE = "UserDataSet.xml";

  @Autowired
  @Qualifier("cflUserNotAuthorizedService")
  private UserNotAuthorizedService userNotAuthorizedService;

  @Before
  public void setUp() throws Exception {
    executeDataSet("datasets/" + USER_DATA_SET_FILE);
  }

  @Test
  public void shouldGetAdminUser() {
    User actual = userNotAuthorizedService.getUser("admin");

    assertNotNull(actual);
    assertEquals(Integer.valueOf(1), actual.getUserId());
    assertEquals("admin", actual.getUsername());
    assertEquals("admin", actual.getSystemId());
  }

  @Test
  public void shouldGetDaemonUser() {
    User actual = userNotAuthorizedService.getUser("daemon");

    assertNotNull(actual);
    assertEquals(Integer.valueOf(2), actual.getUserId());
    assertEquals("daemon", actual.getUsername());
    assertEquals("daemon", actual.getSystemId());
  }

  @Test
  public void shouldGetCfLDoctorUser() {
    User actual = userNotAuthorizedService.getUser("cfldoctor1");

    assertNotNull(actual);
    assertEquals(Integer.valueOf(3), actual.getUserId());
    assertEquals("cfldoctor1", actual.getUsername());
    assertEquals("cfldoctor1", actual.getSystemId());
  }
}
