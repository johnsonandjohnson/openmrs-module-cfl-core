package org.openmrs.module.cfl.api.constant;

public final class ConfigConstants {

    public static final String FIND_PERSON_FILTER_STRATEGY_KEY = "cfl.findPerson.filter.strategy";
    public static final String FIND_PERSON_FILTER_STRATEGY_DEFAULT_VALUE = "cfl.findPersonWithCaregiverRoleStrategy";
    public static final String FIND_PERSON_FILTER_STRATEGY_DESCRIPTION = "Used to specify Spring bean name which points"
            + " on strategy dedicated for filtering people displayed in the people overview (eg. to display only "
            + "caregivers). If empty, results will not be filtered.";

    private ConfigConstants() {
    }
}
