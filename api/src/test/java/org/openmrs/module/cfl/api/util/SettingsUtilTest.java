package org.openmrs.module.cfl.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAttribute;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.contract.countrysettings.Settings;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.builder.CountrySettingBuilder;
import org.openmrs.module.cfl.builder.LocationAttributeTypeBuilder;
import org.openmrs.module.cfl.builder.LocationBuilder;
import org.openmrs.module.cfl.builder.PatientIdentifierBuilder;
import org.openmrs.module.cfl.builder.PersonAttributeBuilder;
import org.openmrs.module.cfl.builder.PersonAttributeTypeBuilder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, Daemon.class})
public class SettingsUtilTest {

  @Mock
  private PatientService patientService;

  @Mock
  private LocationService locationService;

  @Mock
  private ConfigService configService;

  @Mock
  private AdministrationService administrationService;

  private Patient patient;

  private static final String COUNTRY_DECODED_ATTR_TYPE_NAME = "Country decoded";

  @Before
  public void setUp() throws IOException {
    mockStatic(Context.class);
    when(Context.getPatientService()).thenReturn(patientService);
    when(Context.getLocationService()).thenReturn(locationService);
    when(Context.getAdministrationService()).thenReturn(administrationService);
    when(Context.getRegisteredComponent(anyString(), eq(ConfigService.class))).thenReturn(
        configService);

    when(configService.getAllCountrySettings()).thenReturn(
        CountrySettingBuilder.createAllCountrySettings());
    when(locationService.getLocationAttributeTypeByName(COUNTRY_DECODED_ATTR_TYPE_NAME)).thenReturn(
        new LocationAttributeTypeBuilder().build());
  }

  @Test
  public void shouldReturnProperSettingsForMaliCountryAndFrLanguage() {
    patient = createPatient("Mali", "fr");
    when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);

    Settings settings = CountrySettingUtil.getSettingsForPatient(patient);

    assertEquals("Mali_fr_smsConfig", settings.getSms());
    assertEquals("Mali_fr_callConfig", settings.getCall());
    assertTrue(settings.isPerformCallOnPatientRegistration());
    assertTrue(settings.isSendSmsOnPatientRegistration());
    assertFalse(settings.isShouldSendReminderViaCall());
    assertFalse(settings.isShouldSendReminderViaSms());
  }

  @Test
  public void shouldReturnProperSettingsForMaliCountryAndUnknownLanguage() {
    patient = createPatient("Mali", "unknown");
    when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);

    Settings settings = CountrySettingUtil.getSettingsForPatient(patient);

    assertEquals("Mali_default_smsConfig", settings.getSms());
    assertEquals("Mali_default_callConfig", settings.getCall());
    assertTrue(settings.isPerformCallOnPatientRegistration());
    assertFalse(settings.isSendSmsOnPatientRegistration());
    assertFalse(settings.isShouldSendReminderViaCall());
    assertFalse(settings.isShouldSendReminderViaSms());
  }

  @Test
  public void shouldReturnProperSettingsForUnknownCountryAndEsLanguage() {
    patient = createPatient("unknown", "es");
    when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);

    Settings settings = CountrySettingUtil.getSettingsForPatient(patient);

    assertEquals("default_es_smsConfig", settings.getSms());
    assertEquals("default_es_smsConfig", settings.getCall());
    assertTrue(settings.isPerformCallOnPatientRegistration());
    assertTrue(settings.isSendSmsOnPatientRegistration());
    assertTrue(settings.isShouldSendReminderViaCall());
    assertTrue(settings.isShouldSendReminderViaSms());
  }

  @Test
  public void shouldReturnProperSettingsForUnknownCountryAndUnknownLanguage() {
    patient = createPatient("unknown", "unknown");
    when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);

    Settings settings = CountrySettingUtil.getSettingsForPatient(patient);

    assertEquals("default_default_smsConfig", settings.getSms());
    assertEquals("default_default_callConfig", settings.getCall());
    assertTrue(settings.isPerformCallOnPatientRegistration());
    assertTrue(settings.isSendSmsOnPatientRegistration());
    assertTrue(settings.isShouldSendReminderViaCall());
    assertFalse(settings.isShouldSendReminderViaSms());
  }

  private Patient createPatient(String countryName, String languageCode) {
    patient = new Patient();
    patient.addIdentifier(buildPatientIdentifier(countryName));
    patient.addAttribute(buildLanguageAttribute(languageCode));
    return patient;
  }

  private PatientIdentifier buildPatientIdentifier(String countryName) {
    return new PatientIdentifierBuilder()
        .withId(1).withIdentifier("ABC123")
        .withLocation(new LocationBuilder()
            .withId(1).withName("Test location name")
            .withCountry(countryName).build())
        .build();
  }

  private PersonAttribute buildLanguageAttribute(String language) {
    return new PersonAttributeBuilder()
        .withId(Constant.ATTR_ID_1)
        .withPerson(patient)
        .withValue(language)
        .withPersonAttributeType(new PersonAttributeTypeBuilder()
            .withName(CFLConstants.LANGUAGE_ATTRIBUTE_TYPE_NAME).build())
        .build();
  }
}
