package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfldistribution.api.activator.ModuleActivatorStep;

import static org.openmrs.module.cfldistribution.api.activator.impl.ModuleActivatorStepOrderEnum.UPDATE_GLOBAL_PARAMETERS_ACTIVATOR_STEP;

/**
 * Updates Global Parameter values. The step is responsible for updating Global Properties to CFL distribution defaults,
 * from CFL module defaults.
 * <p>
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 * </p>
 */
public class UpdateGlobalParametersActivatorStep implements ModuleActivatorStep {
    @Override
    public int getOrder() {
        return UPDATE_GLOBAL_PARAMETERS_ACTIVATOR_STEP.ordinal();
    }

    @Override
    public void startup(Log log) throws Exception {
        // Enable and keep enabled patient dashboard redirection
        Context
                .getAdministrationService()
                .setGlobalProperty(CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME, Boolean.TRUE.toString());
    }
}
