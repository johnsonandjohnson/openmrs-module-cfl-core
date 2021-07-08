package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cfl.CFLAppsConstants;
import org.openmrs.module.cfldistribution.api.activator.ModuleActivatorStep;

import static org.openmrs.module.cfldistribution.api.activator.impl.ModuleActivatorStepOrderEnum.ENABLE_CFL_APPS_ACTIVATOR_STEP;

/**
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 */
public class EnableCFLAppsActivatorStep implements ModuleActivatorStep {
    @Override
    public int getOrder() {
        return ENABLE_CFL_APPS_ACTIVATOR_STEP.ordinal();
    }

    @Override
    public void startup(Log log) {
        final AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);

        for (String cflAppName : CFLAppsConstants.ALL_NAMES) {
            if (appFrameworkService.getApp(cflAppName) != null) {
                appFrameworkService.enableApp(cflAppName);
                log.info("Enabled CFL App: " + cflAppName);
            } else {
                log.warn("Missing CFL App: " + cflAppName);
            }
        }
    }
}
