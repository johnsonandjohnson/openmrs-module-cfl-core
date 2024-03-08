package org.openmrs.module.cflcore.api.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;
import org.openmrs.module.messages.api.constants.CountryPropertyConstants;
import org.openmrs.module.messages.api.service.CountryPropertyService;
import org.openmrs.test.BaseContextMockTest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.openmrs.module.messages.api.constants.ConfigConstants.DEFAULT_USER_TIMEZONE;

public class ConfigServiceImplTest extends BaseContextMockTest {

  @Test
  public void getSafeMessageDeliveryDate_shouldLimitToAllowedWindowForDifferentTimezones() {
    final AdministrationService administrationService = Mockito.mock(AdministrationService.class);
    when(administrationService.getGlobalProperty(DEFAULT_USER_TIMEZONE)).thenReturn("Asia/Manila");
    this.contextMockHelper.setService(AdministrationService.class, administrationService);

    final CountryPropertyService countryPropertyService =
        Mockito.mock(CountryPropertyService.class);
    this.contextMockHelper.setService(CountryPropertyService.class, countryPropertyService);

    final PersonAttribute bestContactTime = new PersonAttribute();
    bestContactTime.setValue("12:34");
    final Patient testPatient = Mockito.mock(Patient.class);
    when(testPatient.getAttribute("Best contact time")).thenReturn(bestContactTime);

    when(countryPropertyService.getCountryPropertyValueByPerson(
            testPatient.getPerson(),
            CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_NAME))
        .thenReturn(Optional.of("00:01"));
    when(countryPropertyService.getCountryPropertyValueByPerson(
            testPatient.getPerson(),
            CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_NAME))
        .thenReturn(Optional.of("23:59"));

    final PersonService personService = Mockito.mock(PersonService.class);

    final Date testDeliveryDate =
        Date.from(ZonedDateTime.of(2022, 4, 25, 21, 30, 0, 0, ZoneId.of("UTC")).toInstant());

    final ConfigServiceImpl configService = new ConfigServiceImpl();
    configService.setPersonService(personService);

    final Date safeMessageDeliveryDate =
        configService.getSafeMessageDeliveryDate(testPatient, testDeliveryDate);

    assertEquals(testDeliveryDate, safeMessageDeliveryDate);
  }
}
