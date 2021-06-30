package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.openmrs.module.cfldistribution.api.activator.ModuleActivatorStep;
import org.openmrs.module.emrapi.utils.MetadataUtil;

/**
 * The Metadata Package are ZIP files in the classpath, the import mode defined by packages.xml
 * <p>
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 * </p>
 */
public class InstallMetadataPackagesActivatorStep implements ModuleActivatorStep {
    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    public void startup(Log log) throws Exception {
        MetadataUtil.setupStandardMetadata(getClass().getClassLoader());
    }
}
