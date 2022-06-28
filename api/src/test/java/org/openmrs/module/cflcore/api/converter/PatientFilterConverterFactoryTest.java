package org.openmrs.module.cflcore.api.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.TimeZone;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class PatientFilterConverterFactoryTest {
  @Before
  public void mockStatic() {
    PowerMockito.mockStatic(DateUtil.class);
    PowerMockito.when(DateUtil.getDefaultUserTimezone()).thenReturn(TimeZone.getDefault());
  }

  @Test
  public void getPatientFilterConverter_shouldThrowExceptionWhenNoConverterIsFound() {
    final String unknownConverterName = "unknown";

    try {
      PatientFilterConverterFactory.getPatientFilterConverter(unknownConverterName);
      Assert.fail("getPatientFilterConverter should throw IllegalStateException.");
    } catch (IllegalStateException ise) {
      Assert.assertTrue(
          "Error message should contain the missing converter name. ",
          ise.getMessage().contains(unknownConverterName));
    }
  }

  @Test
  public void getPatientFilterConverter_shouldReturnConverterForAllDefaultNames() {
    assertConverterNotNull(PatientFilterAddressFieldStringConverter.class.getSimpleName());
    assertConverterNotNull(PatientFilterAgeToDateConverter.class.getSimpleName());
    assertConverterNotNull(PatientFilterAttributeStringConverter.class.getSimpleName());
    assertConverterNotNull(PatientFilterEntityFieldStringConverter.class.getSimpleName());
    assertConverterNotNull(PatientFilterReceivedDosagesConverter.class.getSimpleName());
    assertConverterNotNull(PatientFilterStringListConverter.class.getSimpleName());
  }

  private void assertConverterNotNull(String converterName) {
    Assert.assertNotNull(
        "Should contain converter: " + converterName,
        PatientFilterConverterFactory.getPatientFilterConverter(converterName));
  }
}
