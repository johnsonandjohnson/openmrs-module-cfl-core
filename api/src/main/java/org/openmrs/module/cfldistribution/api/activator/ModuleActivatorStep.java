package org.openmrs.module.cfldistribution.api.activator;

import org.apache.commons.logging.Log;
import org.openmrs.module.cfldistribution.api.activator.impl.ModuleActivatorStepOrderEnum;

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
    /**
     * <p>
     * <b>Impl note:</b> Use {@link ModuleActivatorStepOrderEnum} to generate an actual value to return by this method.
     * </p>
     *
     * @return the integer used to determine when this step has to execute, lower value goes before higher
     */
    int getOrder();

    void startup(Log log) throws Exception;
}
