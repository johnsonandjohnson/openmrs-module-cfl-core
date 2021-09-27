package org.openmrs.module.cfl.api.constant;

public final class ConfigConstants {

    public static final String FIND_PERSON_FILTER_STRATEGY_KEY = "cfl.findPerson.filter.strategy";
    public static final String FIND_PERSON_FILTER_STRATEGY_DEFAULT_VALUE =
            "cfl.findPersonWithoutRelationshipOrWithCaregiverRoleStrategy";
    public static final String FIND_PERSON_FILTER_STRATEGY_DESCRIPTION = "Used to specify Spring bean name which points"
            + " on strategy dedicated for filtering people displayed in the people overview (eg. to display only "
            + "caregivers). If empty, results will not be filtered.";

    public static final String LAST_VIEWED_PATIENT_SIZE_LIMIT_KEY = "cfl.lastViewedPatientSizeLimit";
    public static final String LAST_VIEWED_PATIENT_SIZE_LIMIT_DEFAULT_VALUE = "50";
    public static final String LAST_VIEWED_PATIENT_SIZE_LIMIT_DESCRIPTION = "Specifies the system wide number of people "
            + "to store as last viewed for a single user, defaults to 50 if not specified";

    private ConfigConstants() {
    }
}
