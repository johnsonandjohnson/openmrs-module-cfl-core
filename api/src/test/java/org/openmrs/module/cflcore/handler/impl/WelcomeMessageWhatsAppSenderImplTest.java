package org.openmrs.module.cflcore.handler.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.module.messages.api.constants.CountryPropertyConstants;
import org.openmrs.module.messages.api.event.SmsEventParamConstants;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;

public class WelcomeMessageWhatsAppSenderImplTest extends AbstractBaseWelcomeMessageSenderImplTest {

  private static final String WHATSAPP_CONFIG_NAME = "nexmo-whatsapp";

  @Mock private ConfigService configService;

  @InjectMocks private WelcomeMessageWhatsAppSenderImpl welcomeMessageWhatsAppSender;

  @Captor private ArgumentCaptor<ScheduledExecutionContext> executionContext;

  @Before
  public void setUp() {
    when(configService.getSafeMessageDeliveryDate(any(), any())).thenReturn(DateUtil.now());
  }

  @Test
  public void shouldVerifyIfProperExecutionContextParamsAreSent() {
    when(personService.getPerson(testPatient.getPersonId())).thenReturn(testPatient.getPerson());
    when(countryPropertyService.getCountryPropertyValueByPerson(
            testPatient.getPerson(),
            CountryPropertyConstants.SEND_WHATSAPP_ON_PATIENT_REGISTRATION_PROP_NAME))
        .thenReturn(Optional.of(Boolean.TRUE.toString()));
    when(countryPropertyService.getCountryPropertyValueByPerson(
            testPatient.getPerson(), CountryPropertyConstants.WHATSAPP_CONFIG_PROP_NAME))
        .thenReturn(Optional.of(WHATSAPP_CONFIG_NAME));

    welcomeMessageWhatsAppSender.send(testPatient);

    verify(messagesDeliveryService).scheduleDelivery(executionContext.capture());
    Map<String, String> channelConfiguration =
        executionContext.getValue().getChannelConfiguration();

    assertThat(
        channelConfiguration.get(SmsEventParamConstants.CONFIG_KEY), is(WHATSAPP_CONFIG_NAME));
  }
}
