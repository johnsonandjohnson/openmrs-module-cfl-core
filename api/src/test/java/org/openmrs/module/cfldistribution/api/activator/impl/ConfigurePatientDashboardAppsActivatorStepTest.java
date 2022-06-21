package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class ConfigurePatientDashboardAppsActivatorStepTest {

  @Mock private AdministrationService administrationService;

  @Mock private AppFrameworkService appFrameworkService;

  private Log log;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    when(Context.getAdministrationService()).thenReturn(administrationService);
    when(Context.getService(AppFrameworkService.class)).thenReturn(appFrameworkService);
  }

  @Test
  public void shouldEnableDefaultConfiguration() {
    when(administrationService.getGlobalProperty("cfl.shouldDisableAppsAndExtensions"))
        .thenReturn("false");

    new ConfigurePatientDashboardAppsActivatorStep().startup(log);

    verify(appFrameworkService).disableExtension(anyString());
    verify(appFrameworkService, times(3)).disableApp(anyString());
  }

  @Test
  public void shouldEnableAdditionalConfiguration() {
    when(administrationService.getGlobalProperty("cfl.shouldDisableAppsAndExtensions"))
        .thenReturn("true");

    new ConfigurePatientDashboardAppsActivatorStep().startup(log);

    verify(appFrameworkService, times(3)).enableApp(anyString());
    verify(appFrameworkService, times(11)).disableApp(anyString());
    verify(appFrameworkService, times(15)).disableExtension(anyString());
  }
}
