/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service;

import org.openmrs.module.cflcore.api.monitor.AllMonitoredStatusData;

/**
 * The MonitoringService Class is an interface of service which provides the Monitoring functionality in the CfL.
 */
public interface MonitoringService {

    /**
     * Gets the current status of all Monitored Components.
     *
     * @return the instance AllMonitoredStatusData, never null
     */
    AllMonitoredStatusData getStatus();

    /**
     * Gets the current state of Monitored Component with {@code name} or null if there is no such component.
     *
     * @param name the name of component to get the status for, not null
     * @return the instance AllMonitoredStatusData with status of single component named {@code name} or null if there is
     * no such monitored component
     */
    AllMonitoredStatusData getStatus(final String name);
}
