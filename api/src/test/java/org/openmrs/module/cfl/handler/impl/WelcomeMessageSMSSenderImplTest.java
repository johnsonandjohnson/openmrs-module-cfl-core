package org.openmrs.module.cfl.handler.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.service.impl.SmsServiceResultsHandlerServiceImpl;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WelcomeMessageSMSSenderImplTest extends AbstractBaseWelcomeMessageSenderImplTest {
    private static final String SMS_CONFIG_NAME = "turnIO";

    @Mock
    private ConfigService configService;

    @InjectMocks
    private WelcomeMessageSMSSenderImpl welcomeMessageSMSSender;

    @Captor
    private ArgumentCaptor<ScheduledExecutionContext> executionContext;

    @Before
    public void setUp() {
        when(configService.getSafeMessageDeliveryDate(any(), any(), any())).thenReturn(DateUtil.now());
    }

    @Test
    public void shouldVerifyIfProperExecutionContextParamsAreSent() {
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setSendSmsOnPatientRegistration(true);
        countrySetting.setSms(SMS_CONFIG_NAME);

        welcomeMessageSMSSender.send(testPatient, countrySetting);

        verify(messagesDeliveryService).scheduleDelivery(executionContext.capture());
        Map<String, String> channelConfiguration = executionContext.getValue().getChannelConfiguration();

        assertThat(channelConfiguration.get(SmsServiceResultsHandlerServiceImpl.SMS_CHANNEL_CONFIG_NAME),
                is(SMS_CONFIG_NAME));
    }
}
