package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.openmrs.module.cfldistribution.api.activator.ModuleActivatorStep;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;

import java.util.Collections;

import static org.openmrs.api.context.Context.getRegisteredComponent;

/**
 * The Metadata Bundles are Java classes (beans) which add metadata to the system.
 * <p>
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 * </p>
 */
public class InstallMetadataBundleActivatorStep implements ModuleActivatorStep {
    @Override
    public int getOrder() {
        return 40;
    }

    @Override
    public void startup(Log log) {
        final MetadataDeployService service = getRegisteredComponent("metadataDeployService", MetadataDeployService.class);
        final MetadataBundle rolesAndPrivileges = getRegisteredComponent("cflRolePrivilegeProfiles", MetadataBundle.class);
        service.installBundles(Collections.singletonList(rolesAndPrivileges));
    }
}
