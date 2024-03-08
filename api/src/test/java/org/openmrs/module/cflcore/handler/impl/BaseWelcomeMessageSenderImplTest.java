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
import org.mockito.Spy;
import org.openmrs.module.cflcore.api.service.impl.ConfigServiceImpl;
import org.openmrs.module.messages.api.constants.CountryPropertyConstants;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BaseWelcomeMessageSenderImplTest extends AbstractBaseWelcomeMessageSenderImplTest {

  @Spy private ConfigServiceImpl configService = new ConfigServiceImpl();

  @Before
  public void setUp() {
    configService.setPersonService(personService);
  }

  @Test
  public void send_shouldScheduleSendingForNow() {
    final TestWelcomeMessageSenderImpl testWelcomeMessageSender =
        prepareTestWelcomeMessageSenderImpl();

    when(countryPropertyService.getCountryPropertyValueByPerson(
            testPatient.getPerson(),
            CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_NAME))
        .thenReturn(Optional.of("00:01"));
    when(countryPropertyService.getCountryPropertyValueByPerson(
            testPatient.getPerson(),
            CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_NAME))
        .thenReturn(Optional.of("23:59"));

    // When
    testWelcomeMessageSender.send(testPatient);

    // Then
    ArgumentCaptor<ScheduledExecutionContext> argumentCaptor =
        ArgumentCaptor.forClass(ScheduledExecutionContext.class);
    verify(messagesDeliveryService).scheduleDelivery(argumentCaptor.capture());

    final ScheduledExecutionContext resultScheduledExecutionContext = argumentCaptor.getValue();
    assertThat(resultScheduledExecutionContext.getChannelType(), is("TEST"));
    assertThat(resultScheduledExecutionContext.getActorId(), is(testPatient.getId()));
    assertThat(resultScheduledExecutionContext.getActorType(), is("Patient"));
    assertThat(resultScheduledExecutionContext.getPatientId(), is(testPatient.getId()));
    assertNotNull(resultScheduledExecutionContext.getServiceIdsToExecute());
    assertThat(resultScheduledExecutionContext.getServiceIdsToExecute().size(), is(1));
    assertThat(Date.from(resultScheduledExecutionContext.getExecutionDate()), is(now));
  }

  @Test
  public void send_shouldScheduleSendingForTomorrowBestContactTime() {
    final TestWelcomeMessageSenderImpl testWelcomeMessageSender =
        prepareTestWelcomeMessageSenderImpl();

    when(countryPropertyService.getCountryPropertyValueByPerson(
            testPatient.getPerson(),
            CountryPropertyConstants.PERFORM_CALL_ON_PATIENT_REGISTRATION_PROP_NAME))
        .thenReturn(Optional.of(Boolean.TRUE.toString()));
    when(countryPropertyService.getCountryPropertyValueByPerson(
            testPatient.getPerson(),
            CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_NAME))
        .thenReturn(Optional.of("11:00"));
    when(countryPropertyService.getCountryPropertyValueByPerson(
            testPatient.getPerson(),
            CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_NAME))
        .thenReturn(Optional.of("15:00"));

    // When
    testWelcomeMessageSender.send(testPatient);

    // Then
    ArgumentCaptor<ScheduledExecutionContext> argumentCaptor =
        ArgumentCaptor.forClass(ScheduledExecutionContext.class);
    verify(messagesDeliveryService).scheduleDelivery(argumentCaptor.capture());

    final ScheduledExecutionContext resultScheduledExecutionContext = argumentCaptor.getValue();
    assertThat(resultScheduledExecutionContext.getChannelType(), is("TEST"));
    assertThat(resultScheduledExecutionContext.getActorId(), is(testPatient.getId()));
    assertThat(resultScheduledExecutionContext.getActorType(), is("Patient"));
    assertThat(resultScheduledExecutionContext.getPatientId(), is(testPatient.getId()));
    assertNotNull(resultScheduledExecutionContext.getServiceIdsToExecute());
    assertThat(resultScheduledExecutionContext.getServiceIdsToExecute().size(), is(1));
    assertThat(
        Date.from(resultScheduledExecutionContext.getExecutionDate()), is(tomorrowBestContactTime));
  }

  private TestWelcomeMessageSenderImpl prepareTestWelcomeMessageSenderImpl() {
    final TestWelcomeMessageSenderImpl testWelcomeMessageSender =
        new TestWelcomeMessageSenderImpl("TEST", true);
    testWelcomeMessageSender.setMessagesDeliveryService(messagesDeliveryService);
    testWelcomeMessageSender.setMessagingGroupService(messagingGroupService);
    testWelcomeMessageSender.setPatientTemplateService(patientTemplateService);
    testWelcomeMessageSender.setPersonService(personService);
    testWelcomeMessageSender.setTemplateService(templateService);
    testWelcomeMessageSender.setConfigService(configService);
    return testWelcomeMessageSender;
  }
}
