package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfldistribution.CfldistributionGlobalParameterConstants;
import org.openmrs.module.cfldistribution.api.activator.ModuleActivatorStep;

import static org.openmrs.module.cfl.api.util.GlobalPropertyUtils.createGlobalSettingIfNotExists;
import static org.openmrs.module.cfldistribution.api.activator.impl.ModuleActivatorStepOrderEnum.CREATE_GLOBAL_PARAMETERS_ACTIVATOR_STEP;

/**
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 */
public class CreateGlobalParametersActivatorStep implements ModuleActivatorStep {
    @Override
    public int getOrder() {
        return CREATE_GLOBAL_PARAMETERS_ACTIVATOR_STEP.ordinal();
    }

    @Override
    public void startup(Log log) {
        createGlobalSettingIfNotExists(CfldistributionGlobalParameterConstants.CFL_DISTRO_BOOTSTRAPPED_KEY,
                CfldistributionGlobalParameterConstants.CFL_DISTRO_BOOTSTRAPPED_DEFAULT_VALUE,
                CfldistributionGlobalParameterConstants.CFL_DISTRO_BOOTSTRAPPED_DEFAULT_DESCRIPTION);
        createGlobalSettingIfNotExists(CfldistributionGlobalParameterConstants.SHOULD_DISABLE_APPS_AND_EXTENSIONS_KEY,
                CfldistributionGlobalParameterConstants.SHOULD_DISABLE_APPS_AND_EXTENSIONS_DEFAULT_VALUE,
                CfldistributionGlobalParameterConstants.SHOULD_DISABLE_APPS_AND_EXTENSIONS_DESCRIPTION);
        createGlobalSettingIfNotExists(CFLConstants.LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME,
                CfldistributionGlobalParameterConstants.CFL_LOCATION_ATTRIBUTE_TYPE_UUID);
    }
}
