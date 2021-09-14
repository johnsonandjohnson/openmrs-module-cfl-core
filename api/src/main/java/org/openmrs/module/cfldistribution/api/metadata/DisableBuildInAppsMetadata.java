package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

/**
 * Disables Apps from CfL dependencies, which are not used by CfL or are replaced by other app.
 */
public class DisableBuildInAppsMetadata extends VersionedMetadataBundle {
    /**
     * Replaced by cflui.findPatient
     */
    private final static String CORE_FIND_PATIENT_APP = "coreapps.findPatient";

    private AppFrameworkService appFrameworkService;

    @Override
    public int getVersion() {
        return 1;
    }

    public void setAppFrameworkService(AppFrameworkService appFrameworkService) {
        this.appFrameworkService = appFrameworkService;
    }

    @Override
    protected void installEveryTime() {
        // nothing to do
    }

    @Override
    protected void installNewVersion() {
        appFrameworkService.disableApp(CORE_FIND_PATIENT_APP);
    }
}
