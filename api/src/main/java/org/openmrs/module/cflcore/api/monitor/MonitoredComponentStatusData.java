/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.monitor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.module.cflcore.api.util.DateUtil;

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
        return new MonitoredComponentStatusData(DateUtil.now(), MonitoringStatus.ERROR, exception.toString());
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
