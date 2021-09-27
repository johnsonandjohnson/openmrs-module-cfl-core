package org.openmrs.module.cfl.api.monitor;

/**
 * The MonitoringStatus enum describes the status of monitored component.
 */
public enum MonitoringStatus {
    /** The component functions as expected. */
    OK,

    /** The component does NOT function as expected. */
    ERROR;
}
