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

import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cflcore.api.monitor.AllMonitoredStatusData;
import org.openmrs.module.cflcore.api.monitor.MonitoredComponentStatusData;
import org.openmrs.module.cflcore.api.monitor.MonitoringStatus;
import org.openmrs.module.cflcore.api.monitor.ComponentMonitoringProvider;
import org.openmrs.module.cflcore.api.service.MonitoringService;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The default implementation of {@link MonitoringService}.
 * <p>
 * Bean is configured in moduleApplicationContext.xml.
 * </p>
 */
public class MonitoringServiceImpl extends BaseOpenmrsService implements MonitoringService {
    private final Map<String, ComponentMonitoringProvider> providers = new HashMap<String, ComponentMonitoringProvider>();

    @Override
    public AllMonitoredStatusData getStatus() {
        return getStatus(providers);
    }

    private AllMonitoredStatusData getStatus(final Map<String, ComponentMonitoringProvider> providerMap) {
        final Date timestamp = new Date();
        final Map<String, MonitoredComponentStatusData> componentsStatus =
                new HashMap<String, MonitoredComponentStatusData>();

        MonitoringStatus monitoringStatus = MonitoringStatus.OK;

        for (final Map.Entry<String, ComponentMonitoringProvider> provider : providerMap.entrySet()) {
            final MonitoredComponentStatusData componentStatusData = getProviderStatus(provider.getValue());

            if (componentStatusData.getMonitoringStatus() == MonitoringStatus.ERROR) {
                monitoringStatus = MonitoringStatus.ERROR;
            }

            componentsStatus.put(provider.getKey(), componentStatusData);
        }

        return new AllMonitoredStatusData(timestamp, monitoringStatus, componentsStatus);
    }

    private MonitoredComponentStatusData getProviderStatus(final ComponentMonitoringProvider provider) {
        try {
            return provider.getStatus();
        } catch (RuntimeException re) {
            return MonitoredComponentStatusData.fromException(re);
        }
    }

    @Override
    public AllMonitoredStatusData getStatus(String name) {
        final ComponentMonitoringProvider selectedProvider = providers.get(name);

        if (selectedProvider == null) {
            return null;
        }

        return getStatus(Collections.singletonMap(name, selectedProvider));
    }

    @Override
    public void onStartup() {
        providers.clear();
        loadProviders();
    }

    private void loadProviders() {
        final List<ComponentMonitoringProvider> providerBeans =
                Context.getRegisteredComponents(ComponentMonitoringProvider.class);

        for (final ComponentMonitoringProvider providerBean : providerBeans) {
            final ComponentMonitoringProvider loadedProvider = providers.get(providerBean.getComponentName());

            if (loadedProvider == null || loadedProvider.getPriority() < providerBean.getPriority()) {
                providers.put(providerBean.getComponentName(), providerBean);
            }
        }
    }
}
