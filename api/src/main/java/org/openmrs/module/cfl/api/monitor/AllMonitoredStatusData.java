package org.openmrs.module.cfl.api.monitor;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The immutable AllMonitoredStatusData contains monitored status of multiple components.
 */
public final class AllMonitoredStatusData {
    private final Date timestamp;
    private final MonitoringStatus monitoringStatus;
    private final Map<String, MonitoredComponentStatusData> componentsStatus;

    /**
     * @param timestamp        the timestamp, not null
     * @param monitoringStatus the status which reprehends the combined status of all items of {@code componentStatus},
     *                         not null
     * @param componentsStatus the Map of statuses for individual components, not null
     * @throws IllegalArgumentException if any of the parameters is null
     */
    public AllMonitoredStatusData(final Date timestamp, MonitoringStatus monitoringStatus,
                                  Map<String, MonitoredComponentStatusData> componentsStatus) {
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp must not be null!");
        }
        if (monitoringStatus == null) {
            throw new IllegalArgumentException("monitoringStatus must not be null!");
        }
        if (componentsStatus == null) {
            throw new IllegalArgumentException("componentsStatus must not be null!");
        }

        this.timestamp = timestamp;
        this.monitoringStatus = monitoringStatus;
        this.componentsStatus =
                Collections.unmodifiableMap(new HashMap<String, MonitoredComponentStatusData>(componentsStatus));
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public MonitoringStatus getMonitoringStatus() {
        return monitoringStatus;
    }

    public Map<String, MonitoredComponentStatusData> getComponentsStatus() {
        return componentsStatus;
    }
}
