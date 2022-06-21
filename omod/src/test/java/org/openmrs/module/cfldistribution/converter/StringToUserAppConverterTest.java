package org.openmrs.module.cfldistribution.converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.UserApp;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class StringToUserAppConverterTest {

  @Mock private AppFrameworkService appFrameworkService;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    when(Context.getService(AppFrameworkService.class)).thenReturn(appFrameworkService);
  }

  @Test
  public void shouldConvertAppIdToUserAppWhenAppIdIsNotNull() {
    new StringToUserAppConverter().convert("testAppId");

    verify(appFrameworkService).getUserApp("testAppId");
  }

  @Test
  public void shouldNotConvertAppIdToUserAppWhenAppIdIsNull() {
    UserApp actual = new StringToUserAppConverter().convert(null);

    assertNull(actual);
  }
}
