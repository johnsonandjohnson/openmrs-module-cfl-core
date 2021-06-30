package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cfl.CFLAppsConstants;
import org.openmrs.module.cfldistribution.api.activator.ModuleActivatorStep;

/**
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 */
public class EnableCFLAppsActivatorStep implements ModuleActivatorStep {
    @Override
    public int getOrder() {
        return 25;
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
