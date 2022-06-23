/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.AtLeast;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.messages.api.constants.ConfigConstants;
import org.openmrs.module.messages.api.dao.PatientAdvancedDao;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.model.ScheduledService;
import org.openmrs.module.messages.api.service.MessagesDeliveryService;
import org.openmrs.module.messages.api.service.MessagingService;
import org.openmrs.module.messages.api.service.PatientTemplateService;
import org.openmrs.module.messages.api.service.ScheduledServiceGroupService;
import org.openmrs.module.messages.api.util.ScheduledExecutionContextUtil;
import org.openmrs.module.messages.domain.criteria.QueryCriteria;
import org.openmrs.module.messages.domain.criteria.ScheduledServiceCriteria;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

public class AdHocMessageServiceImplTest extends BaseModuleContextSensitiveTest {
  private static final String CALL_CHANNEL_CALL_FLOW = "callFlow";
  private final Date now = new Date(1620743880000L); // 2021-05-11T14:38:00UTC

  @Autowired private PatientAdvancedDao patientAdvancedDao;

  @Autowired
  @Qualifier("messages.messagingService")
  private MessagingService messagingService;

  @Autowired private ConfigService configService;

  @Autowired
  @Qualifier("messages.deliveryService")
  private MessagesDeliveryService messagesDeliveryService;

  @Autowired
  @Qualifier("messages.patientTemplateService")
  private PatientTemplateService patientTemplateService;

  @Autowired private ScheduledServiceGroupService scheduledServiceGroupService;

  @Autowired private SchedulerService schedulerServiceMock;

  public AdHocMessageServiceImplTest() throws Exception {
    // Skip because it imports some Patients
    skipBaseSetup();
  }

  @Before
  public void beforeTest() throws Exception {
    executeDataSet("org/openmrs/include/initialInMemoryTestDataSet.xml");
    executeDataSet("datasets/AdHocMessageServiceImplTest.xml");

    this.authenticate();

    Context.getAdministrationService()
        .setGlobalProperty(ConfigConstants.DEFAULT_USER_TIMEZONE, "UTC");
  }

  @Test
  public void scheduleAdHocMessage_shouldScheduleForGivenDateTimeAndAllPatients() {
    // Given
    final AdHocMessageServiceImpl service = prepareAdHocMessageServiceImpl();
    final String channelType = "Call";
    final String callFlowName = "AdHocCallFlow";

    // When
    final Set<Patient> patients =
        new HashSet<Patient>(
            patientAdvancedDao.getPatients(0, -1, QueryCriteria.fromConditions(Patient.class)));

    service.scheduleAdHocMessage(
        now,
        Collections.singleton(channelType),
        singletonMap(CALL_CHANNEL_CALL_FLOW, callFlowName),
        patients);

    // Then
    final List<ScheduledService> allScheduledServices = messagingService.findAllByCriteria(null);
    assertNotNull(allScheduledServices);
    assertThat(allScheduledServices.size(), is(patients.size()));

    final ArgumentCaptor<TaskDefinition> taskDefinitionArgumentCaptor =
        ArgumentCaptor.forClass(TaskDefinition.class);
    verify(schedulerServiceMock, new AtLeast(3))
        .saveTaskDefinition(taskDefinitionArgumentCaptor.capture());

    final Map<String, TaskDefinition> latestTaskDefinitionState = new HashMap<>();
    for (TaskDefinition value : taskDefinitionArgumentCaptor.getAllValues()) {
      latestTaskDefinitionState.put(value.getUuid(), value);
    }

    assertThat(latestTaskDefinitionState.size(), is(patients.size()));

    for (final TaskDefinition taskDefinition : latestTaskDefinitionState.values()) {
      final String executionContextJson = taskDefinition.getProperties().get("EXECUTION_CONTEXT");
      assertNotNull(executionContextJson);

      final ScheduledExecutionContext executionContext =
          ScheduledExecutionContextUtil.fromJson(executionContextJson);

      final List<ScheduledService> scheduledServices =
          messagingService.findAllByCriteria(
              ScheduledServiceCriteria.forIds(executionContext.getServiceIdsToExecute()));

      assertThat(scheduledServices.size(), is(1));
      assertThat(Date.from(executionContext.getExecutionDate()), is(now));
      assertThat(
          Date.from(executionContext.getExecutionDate()),
          is(taskDefinition.getNextExecutionTime()));
      assertThat(executionContext.getChannelType(), is(channelType));
      assertNotNull(executionContext.getChannelConfiguration());
      assertThat(
          executionContext.getChannelConfiguration().get(CALL_CHANNEL_CALL_FLOW), is(callFlowName));
    }
  }

  private AdHocMessageServiceImpl prepareAdHocMessageServiceImpl() {
    final AdHocMessageServiceImpl service = new AdHocMessageServiceImpl();
    service.setConfigService(configService);
    service.setMessagesDeliveryService(messagesDeliveryService);
    service.setPatientTemplateService(patientTemplateService);
    service.setScheduledServiceGroupService(scheduledServiceGroupService);
    return service;
  }
}
