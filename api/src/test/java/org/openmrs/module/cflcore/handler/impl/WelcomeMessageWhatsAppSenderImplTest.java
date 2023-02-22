package org.openmrs.module.cflcore.handler.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.cflcore.api.contract.CountrySetting;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.service.impl.AbstractTextMessageServiceResultsHandlerService;

public class WelcomeMessageWhatsAppSenderImplTest extends AbstractBaseWelcomeMessageSenderImplTest {

  private static final String WHATSAPP_CONFIG_NAME = "nexmo-whatsapp";

  @Mock
  private ConfigService configService;

  @InjectMocks
  private WelcomeMessageWhatsAppSenderImpl welcomeMessageWhatsAppSender;

  @Captor
  private ArgumentCaptor<ScheduledExecutionContext> executionContext;

  @Before
  public void setUp() {
    when(configService.getSafeMessageDeliveryDate(any(), any(), any())).thenReturn(DateUtil.now());
  }

  @Test
  public void shouldVerifyIfProperExecutionContextParamsAreSent() {
    CountrySetting countrySetting = new CountrySetting();
    countrySetting.setSendWhatsAppOnPatientRegistration(true);
    countrySetting.setWhatsApp(WHATSAPP_CONFIG_NAME);

    welcomeMessageWhatsAppSender.send(testPatient, countrySetting);

    verify(messagesDeliveryService).scheduleDelivery(executionContext.capture());
    Map<String, String> channelConfiguration = executionContext.getValue()
      .getChannelConfiguration();

    assertThat(channelConfiguration.get(
      AbstractTextMessageServiceResultsHandlerService.CONFIG_KEY), is(WHATSAPP_CONFIG_NAME));
  }
}
