/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.service.impl;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.module.cfl.api.monitor.AllMonitoredStatusData;
import org.openmrs.module.cfl.api.monitor.MonitoredComponentStatusData;
import org.openmrs.module.cfl.api.monitor.MonitoringStatus;
import org.openmrs.module.cfl.api.monitor.ComponentMonitoringProvider;
import org.openmrs.test.BaseContextMockTest;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.util.Assert.notNull;

public class MonitoringServiceImplTest extends BaseContextMockTest {
    private static final Date TEST_TIMESTAMP = new Date(1614590340000L);

    @Mock
    private ApplicationContext applicationContext;

    @Test
    public void shouldReturnOKWhenNoProvidersExist() {
        // given

        when(applicationContext.getBeansOfType(ComponentMonitoringProvider.class))
                .thenReturn(Collections.<String, ComponentMonitoringProvider>emptyMap());

        final MonitoringServiceImpl testService = new MonitoringServiceImpl();
        testService.onStartup();

        // when
        final AllMonitoredStatusData monitoredStatusData = testService.getStatus();

        // then
        assertEquals(MonitoringStatus.OK, monitoredStatusData.getMonitoringStatus());
    }

    @Test
    public void shouldReturnOKWhenAllProvidersReportOK() {
        // given
        final ComponentMonitoringProvider providerA = okProvider("okComponent1");
        final ComponentMonitoringProvider providerB = okProvider("okComponent2");

        final Map<String, ComponentMonitoringProvider> registeredProviders =
                new HashMap<String, ComponentMonitoringProvider>();
        registeredProviders.put("providerA", providerA);
        registeredProviders.put("providerB", providerB);

        when(applicationContext.getBeansOfType(ComponentMonitoringProvider.class)).thenReturn(registeredProviders);

        final MonitoringServiceImpl testService = new MonitoringServiceImpl();
        testService.onStartup();

        // when
        final AllMonitoredStatusData monitoredStatusData = testService.getStatus();

        // then
        assertEquals(MonitoringStatus.OK, monitoredStatusData.getMonitoringStatus());
        assertEquals(2, monitoredStatusData.getComponentsStatus().size());
    }

    @Test
    public void shouldReturnErrorWhenAtLeastOneProviderError() {
        // given
        final ComponentMonitoringProvider providerA = errorProvider("errorComponent");
        final ComponentMonitoringProvider providerB = okProvider("okComponent");

        final Map<String, ComponentMonitoringProvider> registeredProviders =
                new HashMap<String, ComponentMonitoringProvider>();
        registeredProviders.put("providerA", providerA);
        registeredProviders.put("providerB", providerB);

        when(applicationContext.getBeansOfType(ComponentMonitoringProvider.class)).thenReturn(registeredProviders);

        final MonitoringServiceImpl testService = new MonitoringServiceImpl();
        testService.onStartup();

        // when
        final AllMonitoredStatusData monitoredStatusData = testService.getStatus();

        // then
        assertEquals(MonitoringStatus.ERROR, monitoredStatusData.getMonitoringStatus());
        assertEquals(2, monitoredStatusData.getComponentsStatus().size());
        notNull(monitoredStatusData.getComponentsStatus().get("errorComponent"));
        assertEquals(providerA.getStatus().getMessage(),
                monitoredStatusData.getComponentsStatus().get("errorComponent").getMessage());
    }

    @Test
    public void shouldReturnErrorWhenProviderThrowsException() {
        // given
        final ComponentMonitoringProvider providerA = okProvider("okComponent");
        final ComponentMonitoringProvider providerB = exceptionProvider("exceptionComponent");

        final Map<String, ComponentMonitoringProvider> registeredProviders =
                new HashMap<String, ComponentMonitoringProvider>();
        registeredProviders.put("providerA", providerA);
        registeredProviders.put("providerB", providerB);

        when(applicationContext.getBeansOfType(ComponentMonitoringProvider.class)).thenReturn(registeredProviders);

        final MonitoringServiceImpl testService = new MonitoringServiceImpl();
        testService.onStartup();

        // when
        final AllMonitoredStatusData monitoredStatusData = testService.getStatus();

        // then
        assertEquals(MonitoringStatus.ERROR, monitoredStatusData.getMonitoringStatus());
        assertEquals(2, monitoredStatusData.getComponentsStatus().size());
        notNull(monitoredStatusData.getComponentsStatus().get("exceptionComponent"));
        assertEquals(TestProviderException.TO_STRING,
                monitoredStatusData.getComponentsStatus().get("exceptionComponent").getMessage());
    }

    private ComponentMonitoringProvider okProvider(final String componentName) {
        final MonitoredComponentStatusData ok = new MonitoredComponentStatusData(TEST_TIMESTAMP, MonitoringStatus.OK, "");
        final ComponentMonitoringProvider provideOK = Mockito.mock(ComponentMonitoringProvider.class);
        when(provideOK.getComponentName()).thenReturn(componentName);
        when(provideOK.getStatus()).thenReturn(ok);

        return provideOK;
    }

    private ComponentMonitoringProvider errorProvider(final String componentName) {
        final String errorMsg = "Error provided";
        final MonitoredComponentStatusData error =
                new MonitoredComponentStatusData(TEST_TIMESTAMP, MonitoringStatus.ERROR, errorMsg);
        final ComponentMonitoringProvider provideError = Mockito.mock(ComponentMonitoringProvider.class);
        when(provideError.getComponentName()).thenReturn(componentName);
        when(provideError.getStatus()).thenReturn(error);

        return provideError;
    }

    private ComponentMonitoringProvider exceptionProvider(final String componentName) {
        final ComponentMonitoringProvider provideException = Mockito.mock(ComponentMonitoringProvider.class);
        when(provideException.getComponentName()).thenReturn(componentName);
        when(provideException.getStatus()).thenThrow(new TestProviderException());

        return provideException;
    }

    private static class TestProviderException extends RuntimeException {
        static final String TO_STRING = "TestProviderException{}";

        @Override
        public String toString() {
            return TO_STRING;
        }
    }
}
