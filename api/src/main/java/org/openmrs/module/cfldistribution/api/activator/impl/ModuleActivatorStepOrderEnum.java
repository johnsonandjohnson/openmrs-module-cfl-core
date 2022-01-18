package org.openmrs.module.cfldistribution.api.activator.impl;

/**
 * The enumeration of all activator steps in order of execution.
 * <p>
 * The order in which enum constants appear in the source code directly reflects the order of steps execution.
 * </p>
 */
public enum ModuleActivatorStepOrderEnum {
    /**
     * @see CreateGlobalParametersActivatorStep
     */
    CREATE_GLOBAL_PARAMETERS_ACTIVATOR_STEP,
    /**
     * @see FixRiskFactorForHIVConceptsActivatorStep
     */
    FIX_RISK_FACTOR_FOR_HIV_CONCEPTS_ACTIVATOR_STEP,
    /**
     * @see InstallMetadataPackagesActivatorStep
     */
    INSTALL_METADATA_PACKAGES_ACTIVATOR_STEP,
    /**
     * @see ConfigurePatientDashboardAppsActivatorStep
     */
    CONFIGURE_PATIENT_DASHBOARD_APPS_ACTIVATOR_STEP,
    /**
     * @see InstallMetadataBundleActivatorStep
     */
    INSTALL_METADATA_BUNDLE_ACTIVATOR_STEP
}
