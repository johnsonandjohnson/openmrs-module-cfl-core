/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.web.controller;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.cflcore.api.monitor.AllMonitoredStatusData;
import org.openmrs.module.cflcore.api.monitor.MonitoredComponentStatusData;
import org.openmrs.module.cflcore.api.monitor.MonitoringStatus;
import org.openmrs.module.cflcore.api.service.MonitoringService;
import org.openmrs.module.cflcore.web.monitor.SystemStatusResponseBody;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MonitoringControllerTest {
    private static final Date TEST_TIMESTAMP = new Date(1614590340000L);

    @Mock
    private MonitoringService monitoringService;

    @InjectMocks
    private MonitoringController testMonitoringController;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldHandleNullStatusData() {
        // given
        when(monitoringService.getStatus()).thenReturn(null);

        // when
        final ResponseEntity<SystemStatusResponseBody> response = testMonitoringController.getSystemStatus(null, null);

        // then
        verify(monitoringService, times(1)).getStatus();
        verify(monitoringService, never()).getStatus(anyString());
        assertEquals(200, response.getStatusCode().value());
        assertThat(response.getBody(), Matchers.hasProperty("status", Matchers.equalTo("OK")));
        assertThat(response.getBody(), Matchers.not(Matchers.hasProperty("messages")));
    }

    @Test
    public void shouldHandleOKStatusData() {
        // given
        final AllMonitoredStatusData statusData = new AllMonitoredStatusData(TEST_TIMESTAMP, MonitoringStatus.OK,
                Collections.<String, MonitoredComponentStatusData>emptyMap());

        when(monitoringService.getStatus()).thenReturn(statusData);

        // when
        final ResponseEntity<SystemStatusResponseBody> response = testMonitoringController.getSystemStatus(null, null);

        // then
        verify(monitoringService, times(1)).getStatus();
        verify(monitoringService, never()).getStatus(anyString());
        assertEquals(200, response.getStatusCode().value());
        assertThat(response.getBody(), Matchers.hasProperty("status", Matchers.equalTo("OK")));
        assertThat(response.getBody(), Matchers.hasProperty("messages"));
    }

    @Test
    public void shouldHandleErrorStatusData() {
        // given
        final AllMonitoredStatusData statusData = new AllMonitoredStatusData(TEST_TIMESTAMP, MonitoringStatus.ERROR,
                Collections.<String, MonitoredComponentStatusData>emptyMap());

        when(monitoringService.getStatus()).thenReturn(statusData);

        // when
        final ResponseEntity<SystemStatusResponseBody> response = testMonitoringController.getSystemStatus(null, null);

        // then
        verify(monitoringService, times(1)).getStatus();
        verify(monitoringService, never()).getStatus(anyString());
        assertEquals(500, response.getStatusCode().value());
        assertThat(response.getBody(), Matchers.hasProperty("status", Matchers.equalTo("ERROR")));
        assertThat(response.getBody(), Matchers.hasProperty("messages"));
    }

    @Test
    public void shouldNotIncludeMessageIfStatusOnlyTrue() {
        // given
        final AllMonitoredStatusData statusData = new AllMonitoredStatusData(TEST_TIMESTAMP, MonitoringStatus.ERROR,
                Collections.<String, MonitoredComponentStatusData>emptyMap());

        when(monitoringService.getStatus()).thenReturn(statusData);

        // when
        final ResponseEntity<SystemStatusResponseBody> response = testMonitoringController.getSystemStatus("true", null);

        // then
        verify(monitoringService, times(1)).getStatus();
        verify(monitoringService, never()).getStatus(anyString());
        assertEquals(500, response.getStatusCode().value());
        assertThat(response.getBody(), Matchers.hasProperty("status", Matchers.equalTo("ERROR")));
        assertThat(response.getBody(), Matchers.not(Matchers.hasProperty("messages")));
    }

    @Test
    public void shouldReadStatusForOneComponent() {
        // given
        final String testComponentName = "testComponent";
        final AllMonitoredStatusData statusData = new AllMonitoredStatusData(TEST_TIMESTAMP, MonitoringStatus.ERROR,
                Collections.<String, MonitoredComponentStatusData>emptyMap());

        when(monitoringService.getStatus()).thenReturn(null);
        when(monitoringService.getStatus(testComponentName)).thenReturn(statusData);

        // when
        final ResponseEntity<SystemStatusResponseBody> response =
                testMonitoringController.getSystemStatus(null, testComponentName);

        // then
        verify(monitoringService, never()).getStatus();
        verify(monitoringService, times(1)).getStatus(testComponentName);
        assertEquals(500, response.getStatusCode().value());
        assertThat(response.getBody(), Matchers.hasProperty("status", Matchers.equalTo("ERROR")));
        assertThat(response.getBody(), Matchers.hasProperty("messages"));
    }
}
