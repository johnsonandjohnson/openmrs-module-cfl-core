package org.openmrs.module.cfldistribution.api.activator;

import org.apache.commons.logging.Log;

/**
 * The ModuleActivatorStep Class.
 * <p>
 * The ModuleActivatorSteps are beans which performs single logical step during module startup. E.g.: fix concepts, import
 * necessary data, validate db state.
 * </p>
 * <p>
 * Each implementation has its order value which determines the order in which these steps are executed, the lower the
 * order value the sooner it gets executed.
 * </p>
 * <p>
 * Each step is executed only once during the module startup.
 * </p>
 */
public interface ModuleActivatorStep {
    int getOrder();

    void startup(Log log) throws Exception;
}
