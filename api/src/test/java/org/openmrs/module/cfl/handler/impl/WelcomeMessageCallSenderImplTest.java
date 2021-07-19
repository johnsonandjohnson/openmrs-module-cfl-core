package org.openmrs.module.cfl.handler.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.service.impl.CallFlowServiceResultsHandlerServiceImpl;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WelcomeMessageCallSenderImplTest extends AbstractBaseWelcomeMessageSenderImplTest {

    private static final String CALL_FLOW_NAME = "WelcomeFlow";
    private static final String CALL_CONFIG_NAME = "voxeo";

    @Mock
    private ConfigService configService;

    @InjectMocks
    private WelcomeMessageCallSenderImpl welcomeMessageCallSender;

    @Captor
    private ArgumentCaptor<ScheduledExecutionContext> executionContext;

    @Before
    public void setUp() {
        when(configService.getSafeMessageDeliveryDate(any(), any(), any())).thenReturn(DateUtil.now());
        when(administrationService.getGlobalProperty(CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY))
                .thenReturn(CALL_FLOW_NAME);
    }

    @Test
    public void shouldVerifyIfProperExecutionContextParamsAreSent() {
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setPerformCallOnPatientRegistration(true);
        countrySetting.setCall(CALL_CONFIG_NAME);

        welcomeMessageCallSender.send(new Patient(1), countrySetting);

        verify(messagesDeliveryService).scheduleDelivery(executionContext.capture());
        Map<String, String> channelConfiguration = executionContext.getValue().getChannelConfiguration();

        assertThat(channelConfiguration.get(CallFlowServiceResultsHandlerServiceImpl.CALL_CHANNEL_CONF_FLOW_NAME),
                is(CALL_FLOW_NAME));
        assertThat(channelConfiguration.get(CallFlowServiceResultsHandlerServiceImpl.CALL_CHANNEL_CONFIG_NAME),
                is(CALL_CONFIG_NAME));
    }
}
