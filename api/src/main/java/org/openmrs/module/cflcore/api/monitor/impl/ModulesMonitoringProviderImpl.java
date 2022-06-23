/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.monitor.impl;

import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.cflcore.api.monitor.MonitoredComponentStatusData;
import org.openmrs.module.cflcore.api.monitor.MonitoringStatus;
import org.openmrs.module.cflcore.api.monitor.ComponentMonitoringProvider;
import org.openmrs.module.cflcore.api.util.DateUtil;

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
        final Date timestamp = DateUtil.now();
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
