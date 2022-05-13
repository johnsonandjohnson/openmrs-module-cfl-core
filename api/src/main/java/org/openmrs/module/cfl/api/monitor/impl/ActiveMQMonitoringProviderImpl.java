/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.monitor.impl;

import org.openmrs.event.Event;
import org.openmrs.event.EventMessage;
import org.openmrs.module.cfl.api.monitor.ComponentMonitoringProvider;
import org.openmrs.module.cfl.api.monitor.MonitoredComponentStatusData;
import org.openmrs.module.cfl.api.monitor.MonitoringStatus;
import org.openmrs.module.cfl.api.util.DateUtil;

public class ActiveMQMonitoringProviderImpl implements ComponentMonitoringProvider {
    private static final String COMPONENT_NAME = "ActiveMQ";
    private static final String ERROR_MESSAGE = "ActiveMQ is not running properly";

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
        try {
            Event.fireEvent("testActiveMQ", new EventMessage());
            return new MonitoredComponentStatusData(DateUtil.now(), MonitoringStatus.OK, null);
        } catch (Exception ex) {
            return new MonitoredComponentStatusData(DateUtil.now(), MonitoringStatus.ERROR, ERROR_MESSAGE);
        }
    }
}
