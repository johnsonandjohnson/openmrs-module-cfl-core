package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.openmrs.module.cfldistribution.api.activator.ModuleActivatorStep;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;

import java.util.Collections;

import static org.openmrs.api.context.Context.getRegisteredComponent;
import static org.openmrs.api.context.Context.getRegisteredComponents;
import static org.openmrs.module.cfldistribution.api.activator.impl.ModuleActivatorStepOrderEnum.INSTALL_METADATA_BUNDLE_ACTIVATOR_STEP;

/**
 * The Metadata Bundles are Java classes (beans) which add metadata to the system.
 * <p>
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 * </p>
 */
public class InstallMetadataBundleActivatorStep implements ModuleActivatorStep {
    @Override
    public int getOrder() {
        return INSTALL_METADATA_BUNDLE_ACTIVATOR_STEP.ordinal();
    }

    @Override
    public void startup(Log log) {
        final MetadataDeployService service = getRegisteredComponent("metadataDeployService", MetadataDeployService.class);
        service.installBundles(getRegisteredComponents(MetadataBundle.class));
    }
}
