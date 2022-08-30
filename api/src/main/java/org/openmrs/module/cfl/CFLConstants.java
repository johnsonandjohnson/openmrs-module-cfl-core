package org.openmrs.module.cfl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CFLConstants {

    public static final String MODULE_ID = "cfl";

    public static final String PERSON_DASHBOARD_ATTR_VALUE = "person";
    public static final String PATIENT_DASHBOARD_ATTR_VALUE = "patient";

    public static final String ADMIN_USER_NAME = "admin";

    public static final String SUPER_USER_ROLE_NAME = "Application: Has Super User Privileges";

    /**
     * The name of Full Privilege Level role.
     * <p>
     * This role is managed by Emrapi module (see: EmrApiActivator).
     * </p>
     * */
    public static final String PRIVILEGE_LEVEL_FULL_ROLE_NAME = "Privilege Level: Full";

    public static final String ENROLL_IN_PROGRAM_PRIVILEGE_NAME = "Task: coreapps.enrollInProgram";
    public static final String EDIT_PATIENT_PROGRAM_PRIVILEGE_NAME = "Task: coreapps.editPatientProgram";
    public static final String DELETE_PATIENT_PROGRAM_PRIVILEGE_NAME = "Task: coreapps.deletePatientProgram";
    public static final List<String> PROGRAM_MANAGING_PRIVILEGES_NAMES = Collections.unmodifiableList(Arrays.asList(
            ENROLL_IN_PROGRAM_PRIVILEGE_NAME, EDIT_PATIENT_PROGRAM_PRIVILEGE_NAME, DELETE_PATIENT_PROGRAM_PRIVILEGE_NAME));

    public static final String ACTOR_TYPES_KEY = "messages.actor.types";

    public static final String PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME = "cfl.redirectToPersonDashboard";
    public static final String PATIENT_DASHBOARD_REDIRECT_DEFAULT_VALUE = "true";
    public static final String PATIENT_DASHBOARD_REDIRECT_DESCRIPTION = "true/false flag that decides if CFL module can" +
            " override the patient's coreapps dashboard by redirecting to CFLm person dashboard. Default value: true";

    public static final String LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME = "locationbasedaccess.locationAttributeUuid";
    public static final String LOCATION_ATTRIBUTE_TYPE_UUID = "bfdf8ed0-87e3-437e-897d-81434393a233";

    public static final String TELEPHONE_ATTRIBUTE_NAME = "Telephone Number";

    public static final String UUID_KEY = "uuid";

    public static final String DISABLED_CONTROL_KEY = "cfl.shouldDisableAppsAndExtensions";
    public static final String DISABLED_CONTROL_DEFAULT_VALUE = "true";
    public static final String DISABLED_CONTROL_DESCRIPTION = "Used to determine if the module should disable "
            + "specified apps and extensions on module startup. Possible values: true/false. Note: the server need to "
            + "be restart after change this GP and in order to revert those changes you need to manually clean the "
            + "appframework_component_state table";

    public static final String CAREGIVER_RELATIONSHIP_UUID = "acec590b-825e-45d2-876a-0028f174903d";

    public static final String TRUE = "true";

    public static final String DATETIME_WITH_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String POSSIBLE_RELATIONSHIP_TYPES_KEY = "cfl.possibleRelationshipTypes";
    public static final String POSSIBLE_RELATIONSHIP_TYPES_DESCRIPTION = "Comma separate list of relationship types "
            + "UUIDs which can be use with the CFL person relationship fragment. "
            + "If null then all possible types will be used.";
    public static final String POSSIBLE_RELATIONSHIP_TYPES_DEFAULT_VALUE = "";

    public static final String SUPPORTED_ACTOR_TYPE = "cfl.supportedActorType";
    public static final String SUPPORTED_ACTOR_TYPE_DEFAULT_VALUE = CAREGIVER_RELATIONSHIP_UUID;
    public static final String SUPPORTED_ACTOR_TYPE_DESCRIPTION = "The supported actor type which "
            + "will be used to determine if the redirection to separate dashboard should be shown.";

    public static final String SUPPORTED_ACTOR_TYPE_DIRECTION = "cfl.supportedActorType.actorPosition";
    public static final String SUPPORTED_ACTOR_TYPE_DIRECTION_DEFAULT_VALUE = "A";
    public static final String SUPPORTED_ACTOR_TYPE_DIRECTION_DESCRIPTION = "Determine the position of actor in "
            + "the supported relationship type. Possible values: A, B.";

    public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_NAME = "Person identifier";
    public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_DESCRIPTION = "Person identifier attribute";
    public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_FORMAT = "java.lang.String";
    public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_UUID = "ffb6b2bc-ac7b-4807-8afd-f9464cb14003";

    public static final String PERSON_IDENTIFIER_ATTRIBUTE_KEY = "cfl.person.attribute.identifier";
    public static final String PERSON_IDENTIFIER_ATTRIBUTE_DEFAULT_VALUE = "ffb6b2bc-ac7b-4807-8afd-f9464cb14003";
    public static final String PERSON_IDENTIFIER_ATTRIBUTE_DESCRIPTION = "Used to specify the UUID of person attribute "
            + "type which will be used to store the additional person identifier value.";

    public static final String PERSON_IDENTIFIER_SOURCE_KEY = "cfl.person.identifier.source";
    public static final String PERSON_IDENTIFIER_SOURCE_DEFAULT_VALUE = "1";
    public static final String PERSON_IDENTIFIER_SOURCE_DESCRIPTION = "Used to specify the ID or UUID of idgen identifier "
            + "source which will be used to generate person identifier.";

    public static final String USER_PROPERTY_NAME_LAST_VIEWED_PERSON_IDS = "cfl.lastViewedPersonIds";
    public static final String EVENT_TOPIC_NAME_PERSON_VIEWED = "org.openmrs.module.cfl.api.event.PersonViewed";
    public static final String EVENT_KEY_PERSON_UUID = "personUuid";
    public static final String EVENT_KEY_USER_UUID = "userUuid";

    public static final String PERSON_HEADER_IDENTIFIER_LABEL_KEY = "cfl.personHeader.identifier.label";
    public static final String PERSON_HEADER_IDENTIFIER_LABEL_DEFAULT_VALUE = "cfl.personHeader.identifier.label";
    public static final String PERSON_HEADER_IDENTIFIER_LABEL_DESCRIPTION = "Store the label for person id "
            + "displayed on person header.";

    public static final String HTML_FORM_JQUERY_DATE_FORMAT_KEY = "htmlformentry.jQueryDateFormat";
    public static final String HTML_FORM_JQUERY_DATE_FORMAT_DEFAULT_VALUE = "d M yy";
    public static final String HTML_FORM_JQUERY_DATE_FORMAT_DESCRIPTION = "Display dates in HTML Forms in jQuery (js)" +
     " date format. E.g. 'd M yy' for 31 Jan 2012.";

    public static final String PERSON_LOCATION_ATTRIBUTE_KEY = "cfl.person.attribute.location";
    public static final String PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE = "LocationAttribute";
    public static final String PERSON_LOCATION_ATTRIBUTE_DESCRIPTION = "Specifies attribute name used to store " +
            "information about person localization";

    public static final String CONDITION_LIST_CLASSES_KEY = "coreapps.conditionListClasses";
    public static final String CONDITION_LIST_CLASSES_DEFAULT_VALUE = "d9cab7ff-d2ed-42b2-a2f2-64e677370ff5";
    public static final String CONDITION_LIST_CLASSES_DESCRIPTION = "List of concept uuid's which limits the answers " +
            "for condition list";

    public static final String PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY = "cfl.patientRegistrationCallFlowName";
    public static final String PATIENT_REGISTRATION_CALL_FLOW_NAME_DEFAULT_VALUE = "WelcomeFlow";
    public static final String PATIENT_REGISTRATION_CALL_FLOW_NAME_DESCRIPTION = "Specifies call flow that will be used " +
        "in call after patient registration.";

    public static final String SMS_MESSAGE_AFTER_REGISTRATION_KEY = "cfl.smsMessageAfterRegistration";
    public static final String SMS_MESSAGE_AFTER_REGISTRATION_DEFAULT_VALUE = "{ message: \"Hello, "
            + "you have just registered in our system\" }";
    public static final String SMS_MESSAGE_AFTER_REGISTRATION_DESCRIPTION = "Specifies the SMS message content that will " +
            "be sent immediately after registration";

    public static final String VACCINATION_PROGRAM_KEY = "cfl.vaccines";
    public static final String VACCINATION_PROGRAM_DEFAULT_VALUE = "";
    public static final String VACCINATION_PROGRAM_DESCRIPTION = "Stores information about vaccination programs";

    public static final String OTHER_VISIT_TYPE_NAME = "OTHER";

    public static final String VISIT_STATUS_ATTRIBUTE_TYPE_UUID = "70ca70ac-53fd-49e4-9abe-663d4785fe62";
    public static final String VISIT_STATUS_ATTRIBUTE_TYPE_NAME = "Visit Status";
    public static final String SCHEDULED_VISIT_STATUS = "SCHEDULED";

    public static final String CFL_CONFIG_SERVICE_BEAN_NAME = "cfl.configService";

    public static final String VACCINATION_PROGRAM_ATTRIBUTE_NAME = "Vaccination program";

    public static final String STATUS_OF_OCCURRED_VISIT_KEY = "visits.statusOfOccurredVisit";

    public static final String VACCINATION_INFORMATION_ENABLED_KEY = "cfl.vaccinationInformationEnabled";
    public static final String VACCINATION_INFORMATION_ENABLED_KEY_DEFAULT_VALUE = "false";
    public static final String VACCINATION_INFORMATION_ENABLED_KEY_DESCRIPTION = "Specifies if vaccination information" +
            "is available";

    public static final String UP_WINDOW_ATTRIBUTE_NAME = "Up Window";
    public static final String LOW_WINDOW_ATTRIBUTE_NAME = "Low Window";
    public static final String DOSE_NUMBER_ATTRIBUTE_NAME = "Dose Number";

    public static final String PATIENT_TIMEZONE_LOCATION_ATTR_TYPE_NAME = "Time zone";

    public static final String BEST_CONTACT_TIME_FORMAT = "HH:mm";

    public static final String DEFAULT_USER_TIME_ZONE_GP_NAME = "messages.defaultUserTimezone";

    public static final String COUNTRY_LOCATION_ATTR_TYPE_NAME = "Country decoded";

    public static final String COUNTRY_SETTINGS_MAP_KEY = "cfl.countrySettingsMap";
    public static final String COUNTRY_SETTINGS_MAP_DEFAULT_VALUE = "[\n"
            + "   {\n"
            + "      \"default\":{\n"
            + "         \"SMS\":\"nexmo\",\n"
            + "         \"CALL\":\"nexmo\",\n"
            + "         \"performCallOnPatientRegistration\":false,\n"
            + "         \"sendSmsOnPatientRegistration\":false,\n"
            + "         \"shouldSendReminderViaCall\":false,\n"
            + "         \"shouldSendReminderViaSms\":false\n"
            + "      }\n"
            + "   }\n"
            + "]";
    public static final String COUNTRY_SETTINGS_MAP_DESCRIPTION = "Specifies a map with countries as a keys and " +
            "all country settings as a values";

    public static final String DEFAULT_COUNTRY_SETTING_KEY = "default";

    public static final String SMS_CHANNEL_TYPE = "SMS";

    public static final String CALL_CHANNEL_TYPE = "Call";

    public static final String COMMA_SEPARATOR = ",";

    public static final String USERS_TO_REMOVE = "clerk,nurse,doctor,sysadmin,scheduler";

    public static final String PATIENT_IDENTIFIER_TYPES_TO_REMOVE = "OpenMRS Identification Number," +
            "Old Identification Number,OpenEMPI ID";

    public static final String RELATIONSHIP_TYPES_UUIDS_TO_REMOVE = "8d919b58-c2cc-11de-8d13-0010c6dffd0f," +
            "8d91a01c-c2cc-11de-8d13-0010c6dffd0f,8d91a210-c2cc-11de-8d13-0010c6dffd0f," +
            "8d91a3dc-c2cc-11de-8d13-0010c6dffd0f,2a5f4ff4-a179-4b8a-aa4c-40f71956ebbc";

    public static final String PERSON_ATTRIBUTE_TYPES_TO_REMOVE = "Race,Birthplace,Citizenship,Mother's Name," +
            "Civil Status,Health District,Health Center,Test Patient";

    public static final String PROVIDERS_TO_REMOVE = "clerk,nurse,doctor";

    public static final String LOCATION_UUIDS_TO_REMOVE = "7f65d926-57d6-4402-ae10-a5b3bcbf7986," +
            "7fdfa2cb-bc95-405a-88c6-32b7673c0453,2131aff8-2e2a-480a-b7ab-4ac53250262b," +
            "6351fcf4-e311-4a19-90f9-35667d99a8af,b1a8b05e-3542-4037-bbd3-998ee9c40574," +
            "58c57d25-8d39-41ab-8422-108a0c277d98,aff27d58-a15c-49a6-9beb-d30dcfc0c66e";

    public static final String UNKNOWN_LOCATION_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";

    public static final String LOCATION_UUID_PROPERTY_NAME = "locationUuid";

    public static final String CFL_CLINIC_LOCATION_NAME = "CFL Clinic";

    public static final String LOGIN_LOCATION_TAG_NAME = "Login Location";

    public static final String VISIT_LOCATION_TAG_NAME = "Visit Location";

    public static final String CFL_DISTRO_BOOTSTRAPPED_KEY = "cfl.distro.bootstrapped";

    public static final String LANGUAGE_ATTRIBUTE_TYPE_NAME = "personLanguage";

    private CFLConstants() { }
}
