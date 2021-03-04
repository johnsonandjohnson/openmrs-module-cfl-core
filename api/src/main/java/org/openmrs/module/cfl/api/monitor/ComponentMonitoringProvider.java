package org.openmrs.module.cfl.api.monitor;

/**
 * The ComponentMonitoringProvider Class is an interface of beans which provide Monitoring Status for components.
 * One implementation provides status of the one component.
 * <p>
 * The implementation must be a Spring bean.
 * </p>
 */
public interface ComponentMonitoringProvider {
    /**
     * Gets the name of component which this object provides status for.
     *
     * @return the component name, never null
     */
    String getComponentName();

    /**
     * Gets the priority of this instance.
     * <p>
     * In case if there are two implementations found for the same Component Name, the on with <b>higher</b> priority will
     * be used.
     * </p>
     *
     * @return the priority of this implementations
     */
    int getPriority();

    /**
     * Gets current status of the monitored component.
     * <p>
     * The implementor is responsible for:
     * <ul>
     * <li>handling transaction if transaction is needed to perform its job</li>
     * </ul>
     * </p>
     * <p>
     * Any Runtime Exception thrown by this method is interpreted as ERROR result.
     * </p>
     *
     * @return the Monitored Component Status, never null
     */
    MonitoredComponentStatusData getStatus();
}
