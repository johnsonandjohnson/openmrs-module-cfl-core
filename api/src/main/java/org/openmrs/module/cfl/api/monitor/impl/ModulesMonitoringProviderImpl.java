package org.openmrs.module.cfl.api.monitor.impl;

import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.cfl.api.monitor.MonitoredComponentStatusData;
import org.openmrs.module.cfl.api.monitor.MonitoringStatus;
import org.openmrs.module.cfl.api.monitor.ComponentMonitoringProvider;

import java.util.Collection;
import java.util.Date;

public class ModulesMonitoringProviderImpl implements ComponentMonitoringProvider {
    private static final String COMPONENT_NAME = "Modules";

    private static final String ERROR_MESSAGE_PREFIX = "Not started: ";

    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public MonitoredComponentStatusData getStatus() {
        final Date timestamp = new Date();
        final Collection<Module> loadedModules = ModuleFactory.getLoadedModules();

        final StringBuilder notStartedList = new StringBuilder();
        boolean allStarted = true;

        for (final Module loadedModule : loadedModules) {
            if (!loadedModule.isStarted()) {
                allStarted = false;

                if (notStartedList.length() > 0) {
                    notStartedList.append(", ");
                }

                notStartedList.append(loadedModule.getName());
            }
        }

        if (allStarted) {
            return new MonitoredComponentStatusData(timestamp, MonitoringStatus.OK, null);
        }

        notStartedList.insert(0, ERROR_MESSAGE_PREFIX);

        return new MonitoredComponentStatusData(timestamp, MonitoringStatus.ERROR, notStartedList.toString());
    }
}
