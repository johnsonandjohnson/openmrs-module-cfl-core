package org.openmrs.module.cfl.api.monitor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

/**
 * The immutable MonitoredComponentStatusData contains the component monitoring status and optional
 * information which can clarify the reason of given status.
 */
public final class MonitoredComponentStatusData {
    private final Date timestamp;
    private final MonitoringStatus monitoringStatus;
    private final String message;

    /**
     * @param monitoringStatus the Monitoring Status, not null
     * @param message          the message
     * @throws IllegalArgumentException if {@code monitoringStatus} is null
     */
    public MonitoredComponentStatusData(final Date timestamp, final MonitoringStatus monitoringStatus,
                                        final String message) {
        if (monitoringStatus == null) {
            throw new IllegalArgumentException("monitoringStatus must not be null!");
        }

        this.timestamp = timestamp;
        this.monitoringStatus = monitoringStatus;
        this.message = message;
    }

    /**
     * @param exception the exception to initialize MonitoredComponentStatusData from, not null
     * @return new Instance of MonitoredComponentStatusData initialized with {@code exception}
     * @throws IllegalArgumentException if {@code exception} is null
     */
    public static MonitoredComponentStatusData fromException(final Exception exception) {
        if (exception == null) {
            throw new IllegalArgumentException("exception must not be null!");
        }
        return new MonitoredComponentStatusData(new Date(), MonitoringStatus.ERROR, exception.toString());
    }

    public Date getTimestamp() {
        // Return copy to ensure this object immutable
        return new Date(timestamp.getTime());
    }

    public MonitoringStatus getMonitoringStatus() {
        return monitoringStatus;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
