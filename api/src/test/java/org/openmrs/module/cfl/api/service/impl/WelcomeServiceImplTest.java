package org.openmrs.module.cfl.api.service.impl;

import org.junit.Assert;
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
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.service.WelcomeService;
import org.openmrs.module.cfl.api.util.CountrySettingUtil;
import org.openmrs.module.cfl.api.util.PersonUtil;
import org.openmrs.module.cfl.builder.CountrySettingBuilder;
import org.openmrs.module.messages.api.event.MessagesEvent;
import org.openmrs.module.messages.api.service.MessagesEventService;
import org.openmrs.module.messages.api.service.impl.VelocityNotificationTemplateServiceImpl;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.module.cfl.builder.CountrySettingBuilder.COLUMBIA_SMS_CONFIG;
import static org.openmrs.module.cfl.builder.CountrySettingBuilder.PHIPPIPINES_CALL_CONFIG;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, CountrySettingUtil.class, PersonUtil.class })
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

    @Captor
    private ArgumentCaptor<MessagesEvent> messagesEvent;

    private Map<String, CountrySetting> countrySettingMap;

    private static final String PARSED_TEMPLATE = "{message: \"test message\"}";

    @Before
    public void setUp() throws IOException {
        mockStatic(Context.class, CountrySettingUtil.class, PersonUtil.class);

        countrySettingMap = CountrySettingBuilder.createCountrySettingMap();

        when(Context.getRegisteredComponent("messages.messagesEventService", MessagesEventService.class))
                .thenReturn(messagesEventService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getRegisteredComponent("messages.velocityNotificationTemplateServiceImpl",
                VelocityNotificationTemplateServiceImpl.class)).thenReturn(velocityNotificationTemplateService);
        when(velocityNotificationTemplateService.buildMessageByGlobalProperty(anyMap(), anyString()))
                .thenReturn(PARSED_TEMPLATE);
        when(patientService.getPatient(anyInt())).thenReturn(null);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(administrationService.getGlobalProperty(anyString())).thenReturn("any");
        when(administrationService.getGlobalProperty(anyString(), anyString())).thenReturn("any");
        when(PersonUtil.getPhoneNumber(person)).thenReturn("11111111111");
    }

    @Test
    public void shouldSendSMSForColumbiaConfig() {
        when(CountrySettingUtil.getCountrySettingForPatient(person))
                .thenReturn(countrySettingMap.get(CountrySettingBuilder.COLUMBIA));

        welcomeService.sendWelcomeMessages(person);

        verify(messagesEventService).sendEventMessage(messagesEvent.capture());
        MessagesEvent actual = messagesEvent.getValue();

        Assert.assertThat(actual.getParameters().get("config").toString(), is(COLUMBIA_SMS_CONFIG));
    }

    @Test
    public void shouldPerformCallForPhippipinesConfig() {
        when(CountrySettingUtil.getCountrySettingForPatient(person))
                .thenReturn(countrySettingMap.get(CountrySettingBuilder.PHIPPIPINES));

        welcomeService.sendWelcomeMessages(person);

        verify(messagesEventService).sendEventMessage(messagesEvent.capture());
        MessagesEvent actual = messagesEvent.getValue();

        Assert.assertThat(actual.getParameters().get("config").toString(), is(PHIPPIPINES_CALL_CONFIG));
    }
}
