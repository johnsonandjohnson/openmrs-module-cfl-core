package org.openmrs.module.cfl.api.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.contract.countrysettings.AllCountrySettings;
import org.openmrs.module.cfl.api.contract.countrysettings.Settings;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.WelcomeService;
import org.openmrs.module.cfl.api.util.CountrySettingUtil;
import org.openmrs.module.cfl.api.util.PersonUtil;
import org.openmrs.module.cfl.builder.CountrySettingBuilder;
import org.openmrs.module.messages.api.event.MessagesEvent;
import org.openmrs.module.messages.api.service.MessagesEventService;
import org.openmrs.module.messages.api.service.impl.VelocityNotificationTemplateServiceImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, CountrySettingUtil.class, PersonUtil.class})
public class WelcomeServiceImplTest {

  @InjectMocks
  private WelcomeService welcomeService = new WelcomeServiceImpl();

  @Mock
  private Person person;

  @Mock
  private MessagesEventService messagesEventService;

  @Mock
  private PatientService patientService;

  @Mock
  private AdministrationService administrationService;

  @Mock
  private VelocityNotificationTemplateServiceImpl velocityNotificationTemplateService;

  @Mock
  private ConfigService configService;

  @Captor
  private ArgumentCaptor<MessagesEvent> messagesEvent;

  private AllCountrySettings allCountrySettings;

  private static final String PARSED_TEMPLATE = "{message: \"test message\"}";

  @Before
  public void setUp() throws IOException {
    mockStatic(Context.class, CountrySettingUtil.class, PersonUtil.class);

    allCountrySettings = CountrySettingBuilder.createAllCountrySettings();

    when(
        Context.getRegisteredComponent("messages.messagesEventService", MessagesEventService.class))
        .thenReturn(messagesEventService);
    when(Context.getPatientService()).thenReturn(patientService);
    when(Context.getRegisteredComponent("messages.velocityNotificationTemplateServiceImpl",
        VelocityNotificationTemplateServiceImpl.class)).thenReturn(
        velocityNotificationTemplateService);
    when(velocityNotificationTemplateService.buildMessageByGlobalProperty(anyMap(), anyString()))
        .thenReturn(PARSED_TEMPLATE);
    when(patientService.getPatient(anyInt())).thenReturn(null);
    when(Context.getAdministrationService()).thenReturn(administrationService);
    when(administrationService.getGlobalProperty(anyString())).thenReturn("any");
    when(administrationService.getGlobalProperty(anyString(), anyString())).thenReturn("any");
    when(PersonUtil.getPhoneNumber(person)).thenReturn("11111111111");

    PowerMockito.when(Context.getRegisteredComponent(anyString(), eq(ConfigService.class)))
        .thenReturn(
            configService);

    PowerMockito.when(configService.getAllCountrySettings()).thenReturn(
        CountrySettingBuilder.createAllCountrySettings());
  }

  @Test
  public void shouldReturnCallConfigNameForMaliCountryAndDefaultLanguage() {
    Settings maliCountryWithDefaultLanguageSettings = allCountrySettings.getCountryConfigs()
        .get(0).getLanguageCountrySettingMap().get(0).getSettings();
    when(CountrySettingUtil.getSettingsForPatient(person))
        .thenReturn(maliCountryWithDefaultLanguageSettings);

    welcomeService.sendWelcomeMessages(person);

    verify(messagesEventService).sendEventMessage(messagesEvent.capture());
    MessagesEvent actual = messagesEvent.getValue();

    assertEquals("Mali_default_callConfig", actual.getParameters().get("config").toString());
  }

  @Test
  public void shouldReturnSMSConfigNameForDefaultCountryAndDeLanguage() {
    Settings maliCountryWithDefaultLanguageSettings = allCountrySettings.getCountryConfigs()
        .get(1).getLanguageCountrySettingMap().get(2).getSettings();
    when(CountrySettingUtil.getSettingsForPatient(person))
        .thenReturn(maliCountryWithDefaultLanguageSettings);

    welcomeService.sendWelcomeMessages(person);

    verify(messagesEventService).sendEventMessage(messagesEvent.capture());
    MessagesEvent actual = messagesEvent.getValue();

    assertEquals("default_de_smsConfig", actual.getParameters().get("config").toString());
  }
}
