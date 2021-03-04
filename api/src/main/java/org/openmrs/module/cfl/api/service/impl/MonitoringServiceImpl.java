package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.monitor.AllMonitoredStatusData;
import org.openmrs.module.cfl.api.monitor.MonitoredComponentStatusData;
import org.openmrs.module.cfl.api.monitor.MonitoringStatus;
import org.openmrs.module.cfl.api.monitor.ComponentMonitoringProvider;
import org.openmrs.module.cfl.api.service.MonitoringService;

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
