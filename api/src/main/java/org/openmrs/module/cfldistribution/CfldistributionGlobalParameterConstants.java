package org.openmrs.module.cfldistribution;

public final class CfldistributionGlobalParameterConstants {
    public static final String CFL_DISTRO_BOOTSTRAPPED_KEY = "cfl.distro.bootstrapped";
    public static final String CFL_DISTRO_BOOTSTRAPPED_DEFAULT_VALUE = "false";
    public static final String CFL_DISTRO_BOOTSTRAPPED_DEFAULT_DESCRIPTION = "The global property which indicates that CFL" +
            " distribution has been installed and all 'run only once' updates has been executed.";

    public static final String SHOULD_DISABLE_APPS_AND_EXTENSIONS_KEY = "cfl.shouldDisableAppsAndExtensions";
    public static final String SHOULD_DISABLE_APPS_AND_EXTENSIONS_DEFAULT_VALUE = "true";
    public static final String SHOULD_DISABLE_APPS_AND_EXTENSIONS_DESCRIPTION =
            "Used to determine if the module should disable " +
                    "specified apps and extensions on module startup. Possible values: true/false. Note: the server need " +
                    "to " +
                    "be restart after change this GP and in order to revert those changes you need to manually clean the " +
                    "appframework_component_state table";

    public static final String CFL_LOCATION_ATTRIBUTE_TYPE_UUID = "0a93cbc6-5d65-4886-8091-47a25d3df944";

    public static final String CFL_TELEPHONE_NUMBER_PERSON_ATTRIBUTE_TYPE_UUID = "14d4f066-15f5-102d-96e4-000c29c2a5d7";

    public static final String CFL_EMAIL_ADDRESS_PERSON_ATTRIBUTE_TYPE_UUID = "58cb9e76-75a1-4f49-956f-4ff6d0e02312";

    private CfldistributionGlobalParameterConstants() {

    }
}
