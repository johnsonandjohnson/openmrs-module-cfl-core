/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.handler.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.contract.CountrySetting;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.util.DateUtil;
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

        welcomeMessageCallSender.send(testPatient, countrySetting);

        verify(messagesDeliveryService).scheduleDelivery(executionContext.capture());
        Map<String, String> channelConfiguration = executionContext.getValue().getChannelConfiguration();

        assertThat(channelConfiguration.get(CallFlowServiceResultsHandlerServiceImpl.CALL_CHANNEL_CONF_FLOW_NAME),
                is(CALL_FLOW_NAME));
        assertThat(channelConfiguration.get(CallFlowServiceResultsHandlerServiceImpl.CALL_CHANNEL_CONFIG_NAME),
                is(CALL_CONFIG_NAME));
    }
}
