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

public class WelcomeMessageSMSSenderImplTest extends AbstractBaseWelcomeMessageSenderImplTest {

  private static final String SMS_CONFIG_NAME = "turnIO";

  @Mock private ConfigService configService;

  @InjectMocks private WelcomeMessageSMSSenderImpl welcomeMessageSMSSender;

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
            CountryPropertyConstants.SEND_SMS_ON_PATIENT_REGISTRATION_PROP_NAME))
        .thenReturn(Optional.of(Boolean.TRUE.toString()));
    when(countryPropertyService.getCountryPropertyValueByPerson(
            testPatient.getPerson(), CountryPropertyConstants.SMS_CONFIG_PROP_NAME))
        .thenReturn(Optional.of(SMS_CONFIG_NAME));

    welcomeMessageSMSSender.send(testPatient);

    verify(messagesDeliveryService).scheduleDelivery(executionContext.capture());
    Map<String, String> channelConfiguration =
        executionContext.getValue().getChannelConfiguration();

    assertThat(channelConfiguration.get(SmsEventParamConstants.CONFIG_KEY), is(SMS_CONFIG_NAME));
  }
}
