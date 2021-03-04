package org.openmrs.module.cfl.api.service;

import org.openmrs.module.cfl.api.monitor.AllMonitoredStatusData;

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
