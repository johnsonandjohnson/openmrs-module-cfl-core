package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfldistribution.CfldistributionGlobalParameterConstants;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class CreateGlobalParametersActivatorStepTest {

  @Mock private AdministrationService administrationService;

  private Log log;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    when(Context.getAdministrationService()).thenReturn(administrationService);
  }

  @Test
  public void shouldCreateGlobalPropertiesIfNotExist() {
    new CreateGlobalParametersActivatorStep().startup(log);

    verify(administrationService)
        .getGlobalProperty(CfldistributionGlobalParameterConstants.CFL_DISTRO_BOOTSTRAPPED_KEY);
    verify(administrationService)
        .getGlobalProperty(
            CfldistributionGlobalParameterConstants.SHOULD_DISABLE_APPS_AND_EXTENSIONS_KEY);
    verify(administrationService)
        .getGlobalProperty(CFLConstants.LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME);
  }
}
