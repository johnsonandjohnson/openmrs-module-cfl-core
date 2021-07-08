package org.openmrs.module.cfl.api.metadata;

import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cfl.CFLAppsConstants;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

/**
 * The Metadata Bundle responsible for disabling CFL-specific Apps. These apps has to be explicitly enabled by system user.
 * <p>
 * The DisableCFLAppsBundle disables apps only once, for a first start.
 * </p>
 */
public class DisableCFLAppsBundle extends VersionedMetadataBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installEveryTime() {
        // nothing to do
    }

    @Override
    protected void installNewVersion() {
        final AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);

        for (String cflAppName : CFLAppsConstants.ALL_NAMES) {
            if (appFrameworkService.getApp(cflAppName) != null) {
                appFrameworkService.disableApp(cflAppName);
            } else {
                log.warn("Missing CFL App: " + cflAppName);
            }
        }
    }
}
