/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore;

import org.openmrs.customdatatype.datatype.FreeTextDatatype;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CFLConstants {

  public static final String MODULE_ID = "cflcore";
  public static final String MODULE_API_PACKAGE = "org.openmrs.module.cflcore.api";


  public static final String PERSON_DASHBOARD_ATTR_VALUE = "person";
  public static final String PATIENT_DASHBOARD_ATTR_VALUE = "patient";

  public static final String ADMIN_USER_NAME = "admin";

  public static final String SUPER_USER_ROLE_NAME = "Application: Has Super User Privileges";

  /**
   * The name of Full Privilege Level role.
   *
   * <p>This role is managed by Emrapi module (see: EmrApiActivator).
   */
  public static final String PRIVILEGE_LEVEL_FULL_ROLE_NAME = "Privilege Level: Full";

  public static final String ENROLL_IN_PROGRAM_PRIVILEGE_NAME = "Task: coreapps.enrollInProgram";
  public static final String EDIT_PATIENT_PROGRAM_PRIVILEGE_NAME =
      "Task: coreapps.editPatientProgram";
  public static final String DELETE_PATIENT_PROGRAM_PRIVILEGE_NAME =
      "Task: coreapps.deletePatientProgram";
  public static final List<String> PROGRAM_MANAGING_PRIVILEGES_NAMES =
      Collections.unmodifiableList(
          Arrays.asList(
              ENROLL_IN_PROGRAM_PRIVILEGE_NAME,
              EDIT_PATIENT_PROGRAM_PRIVILEGE_NAME,
              DELETE_PATIENT_PROGRAM_PRIVILEGE_NAME));

  public static final String ACTOR_TYPES_KEY = "messages.actor.types";

  public static final String PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME =
      "cfl.redirectToPersonDashboard";
  public static final String PATIENT_DASHBOARD_REDIRECT_DEFAULT_VALUE = "false";
  public static final String PATIENT_DASHBOARD_REDIRECT_DESCRIPTION =
      "true/false flag that decides if CFL module can"
          + " override the patient's coreapps dashboard by redirecting to CFLm person dashboard. Default value: false "
          + "(true in CfL-only distribution)";

  public static final String LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME =
      "locationbasedaccess.locationAttributeUuid";

  public static final String TELEPHONE_ATTRIBUTE_NAME = "Telephone Number";

  public static final String ACTION_KEY = "action";
  public static final String UUID_KEY = "uuid";

  public static final String CAREGIVER_RELATIONSHIP_UUID = "acec590b-825e-45d2-876a-0028f174903d";

  public static final String DATETIME_WITH_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  public static final String POSSIBLE_RELATIONSHIP_TYPES_KEY = "cfl.possibleRelationshipTypes";
  public static final String POSSIBLE_RELATIONSHIP_TYPES_DESCRIPTION =
      "Comma separate list of relationship types "
          + "UUIDs which can be use with the CFL person relationship fragment. "
          + "If null then all possible types will be used.";
  public static final String POSSIBLE_RELATIONSHIP_TYPES_DEFAULT_VALUE = "";

  public static final String SUPPORTED_ACTOR_TYPE = "cfl.supportedActorType";
  public static final String SUPPORTED_ACTOR_TYPE_DEFAULT_VALUE = CAREGIVER_RELATIONSHIP_UUID;
  public static final String SUPPORTED_ACTOR_TYPE_DESCRIPTION =
      "The supported actor type which "
          + "will be used to determine if the redirection to separate dashboard should be shown.";

  public static final String SUPPORTED_ACTOR_TYPE_DIRECTION =
      "cfl.supportedActorType.actorPosition";
  public static final String SUPPORTED_ACTOR_TYPE_DIRECTION_DEFAULT_VALUE = "A";
  public static final String SUPPORTED_ACTOR_TYPE_DIRECTION_DESCRIPTION =
      "Determine the position of actor in "
          + "the supported relationship type. Possible values: A, B.";

  public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_NAME = "Person identifier";
  public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_DESCRIPTION =
      "Person identifier attribute";
  public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_FORMAT = "java.lang.String";
  public static final String PERSON_IDENTIFIER_ATTRIBUTE_TYPE_UUID =
      "ffb6b2bc-ac7b-4807-8afd-f9464cb14003";

  public static final String PERSON_IDENTIFIER_ATTRIBUTE_KEY = "cfl.person.attribute.identifier";
  public static final String PERSON_IDENTIFIER_ATTRIBUTE_DEFAULT_VALUE =
      "ffb6b2bc-ac7b-4807-8afd-f9464cb14003";
  public static final String PERSON_IDENTIFIER_ATTRIBUTE_DESCRIPTION =
      "Used to specify the UUID of person attribute "
          + "type which will be used to store the additional person identifier value.";

  public static final String PERSON_IDENTIFIER_SOURCE_KEY = "cfl.person.identifier.source";
  public static final String PERSON_IDENTIFIER_SOURCE_DEFAULT_VALUE = "1";
  public static final String PERSON_IDENTIFIER_SOURCE_DESCRIPTION =
      "Used to specify the ID or UUID of idgen identifier "
          + "source which will be used to generate person identifier.";

  public static final String USER_PROPERTY_NAME_LAST_VIEWED_PERSON_IDS = "cfl.lastViewedPersonIds";
  public static final String EVENT_TOPIC_NAME_PERSON_VIEWED =
      "org.openmrs.module.cfl.api.event.PersonViewed";
  public static final String EVENT_KEY_PERSON_UUID = "personUuid";
  public static final String EVENT_KEY_USER_UUID = "userUuid";

  public static final String PERSON_HEADER_IDENTIFIER_LABEL_KEY =
      "cfl.personHeader.identifier.label";
  public static final String PERSON_HEADER_IDENTIFIER_LABEL_DEFAULT_VALUE =
      "cfl.personHeader.identifier.label";
  public static final String PERSON_HEADER_IDENTIFIER_LABEL_DESCRIPTION =
      "Store the label for person id " + "displayed on person header.";

  public static final String HTML_FORM_JQUERY_DATE_FORMAT_KEY = "htmlformentry.jQueryDateFormat";
  public static final String HTML_FORM_JQUERY_DATE_FORMAT_DEFAULT_VALUE = "d M yy";
  public static final String HTML_FORM_JQUERY_DATE_FORMAT_DESCRIPTION =
      "Display dates in HTML Forms in jQuery (js)" + " date format. E.g. 'd M yy' for 31 Jan 2012.";

  public static final String PERSON_LOCATION_ATTRIBUTE_KEY = "cfl.person.attribute.location";
  public static final String PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE = "LocationAttribute";
  public static final String PERSON_LOCATION_ATTRIBUTE_DESCRIPTION =
      "Specifies attribute name used to store " + "information about person localization";

  public static final String CONDITION_LIST_CLASSES_KEY = "coreapps.conditionListClasses";
  public static final String CONDITION_LIST_CLASSES_DEFAULT_VALUE =
      "d9cab7ff-d2ed-42b2-a2f2-64e677370ff5";
  public static final String CONDITION_LIST_CLASSES_DESCRIPTION =
      "List of concept uuid's which limits the answers " + "for condition list";

  public static final String NOTIFICATION_TEMPLATE_WELCOME_MESSAGE =
      "messages.notificationTemplate.welcome-message";
  public static final String NOTIFICATION_TEMPLATE_WELCOME_MESSAGE_VALUE =
      "{ message: \"Hello, you have just registered in our system\" }";
  public static final String NOTIFICATION_TEMPLATE_WELCOME_MESSAGE_DESCRIPTION =
      "Specifies the SMS message content that will be sent immediately after registration";

  public static final String PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY =
      "cfl.patientRegistrationCallFlowName";
  public static final String PATIENT_REGISTRATION_CALL_FLOW_NAME_DEFAULT_VALUE = "";
  public static final String PATIENT_REGISTRATION_CALL_FLOW_NAME_DESCRIPTION =
      "Specifies call flow that will be used in call after patient registration.";

  public static final String VACCINATION_PROGRAM_KEY = "cfl.vaccines";
  public static final String VACCINATION_PROGRAM_DEFAULT_VALUE = "";
  public static final String VACCINATION_PROGRAM_DESCRIPTION =
      "Stores information about vaccination programs";

  public static final String ENVIRONMENT_KEY = "cfl.environment";
  public static final String ENVIRONMENT_DEFAULT_VALUE = "";
  public static final String ENVIRONMENT_DESCRIPTION =
      "Set the value to 'Staging' to hide the Logo";

  public static final String OTHER_VISIT_TYPE_NAME = "OTHER";
  public static final String DOSING_VISIT_TYPE_NAME = "Dosing";

  public static final String VISIT_STATUS_ATTRIBUTE_TYPE_UUID =
      "70ca70ac-53fd-49e4-9abe-663d4785fe62";
  public static final String VISIT_STATUS_ATTRIBUTE_TYPE_NAME = "Visit Status";
  public static final String VISIT_TIME_ATTRIBUTE_TYPE_NAME = "Visit Time";
  public static final String SCHEDULED_VISIT_STATUS = "SCHEDULED";

  public static final String CFL_CONFIG_SERVICE_BEAN_NAME = "cfl.configService";

  public static final String VACCINATION_PROGRAM_ATTRIBUTE_NAME = "Vaccination program";

  public static final String STATUS_OF_OCCURRED_VISIT_KEY = "visits.statusOfOccurredVisit";

  public static final String VACCINATION_INFORMATION_ENABLED_KEY =
      "cfl.vaccinationInformationEnabled";
  public static final String VACCINATION_INFORMATION_ENABLED_KEY_DEFAULT_VALUE = "false";
  public static final String VACCINATION_INFORMATION_ENABLED_KEY_DESCRIPTION =
      "Specifies if vaccination information" + "is available";

  public static final String UP_WINDOW_ATTRIBUTE_NAME = "Up Window";
  public static final String LOW_WINDOW_ATTRIBUTE_NAME = "Low Window";
  public static final String DOSE_NUMBER_ATTRIBUTE_NAME = "Dose Number";

  public static final String PATIENT_TIMEZONE_LOCATION_ATTR_TYPE_NAME = "Time zone";

  public static final String BEST_CONTACT_TIME_FORMAT = "HH:mm";

  public static final String DEFAULT_USER_TIME_ZONE_GP_NAME = "messages.defaultUserTimezone";

  public static final String COUNTRY_LOCATION_ATTR_TYPE_NAME = "Country decoded";

  public static final String SMS_CHANNEL_TYPE = "SMS";

  public static final String CALL_CHANNEL_TYPE = "Call";

  public static final String COMMA_SEPARATOR = ",";

  /**
   * The name of message Template for Welcome Message.
   */
  public static final String WELCOME_MESSAGE_TEMPLATE = "Welcome Message";

  /**
   * The name of Message Template for general-purpose Ad hoc messages.
   */
  public static final String AD_HOC_MESSAGE_TEMPLATE_NAME = "Ad hoc Message";

  public static final String AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_KEY =
      "cfl.adHocMessage.ui.configuration";
  public static final String AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_DESCRIPTION =
      "The Global Property with JSON configuration of the Patient filters available to be used to specify message "
          + "recipients.";
  public static final String AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_DEFAULT_VALUE =
      "[\n"
          + "  {\n"
          + "    \"label\": \"cfl.adHocMessage.patientAddress.country\",\n"
          + "    \"inputType\": \"STRING\",\n"
          + "    \"converter\": \"PatientFilterAddressFieldStringConverter\",\n"
          + "    \"config\": \n"
          + "    {\n"
          + "      \"fieldPath\": \"country\",\n"
          + "      \"operator\": \"=\"\n"
          + "    }\n"
          + "  },\n"
          + "  {\n"
          + "    \"label\": \"cfl.adHocMessage.patientAttribute.LocationAttribute\",\n"
          + "    \"inputType\": \"SELECT_ENTITY_UUID\",\n"
          + "    \"converter\": \"PatientFilterAttributeStringConverter\",\n"
          + "    \"config\": \n"
          + "    {\n"
          + "      \"fieldPath\": \"LocationAttribute\",\n"
          + "      \"operator\": \"=\",\n"
          + "      \"options\": \n"
          + "      [ \n"
          + "        { \"label\": \"CFL Clinic\", \"value\": \"8d6c993e-c2cc-11de-8d13-0010c6dffd0f\" } \n"
          + "      ]\n"
          + "    }\n"
          + "  },\n"
          + "  {\n"
          + "    \"label\": \"cfl.adHocMessage.patientAttribute.Vaccination_program\",\n"
          + "    \"inputType\": \"STRING\",\n"
          + "    \"converter\": \"PatientFilterAttributeStringConverter\",\n"
          + "    \"config\": \n"
          + "    {\n"
          + "      \"fieldPath\": \"Vaccination program\",\n"
          + "      \"operator\": \"=\"\n"
          + "    }\n"
          + "  },\n"
          + "  {\n"
          + "    \"label\": \"cfl.adHocMessage.custom.ReceivedDosages\",\n"
          + "    \"inputType\": \"INTEGER\",\n"
          + "    \"converter\": \"PatientFilterReceivedDosagesConverter\",\n"
          + "    \"config\": \n"
          + "    {\n"
          + "      \"options\": [ \"=\", \">\", \"<\", \">=\", \"<=\" ]\n"
          + "    }\n"
          + "  },\n"
          + "  {\n"
          + "    \"label\": \"cfl.adHocMessage.patient.gender\",\n"
          + "    \"inputType\": \"MULTI_SELECT_STRING\",\n"
          + "    \"converter\": \"PatientFilterStringListConverter\",\n"
          + "    \"config\": \n"
          + "    {\n"
          + "      \"fieldPath\": \"gender\",\n"
          + "      \"options\": [ \"M\", \"F\", \"O\" ]\n"
          + "    }\n"
          + "  },\n"
          + "  {\n"
          + "    \"label\": \"cfl.adHocMessage.patient.age\",\n"
          + "    \"inputType\": \"AGE_RANGE\",\n"
          + "    \"converter\": \"PatientFilterAgeToDateConverter\",\n"
          + "    \"config\": \n"
          + "    {\n"
          + "      \"fieldPath\": \"birthdate\"\n"
          + "    }\n"
          + "  }\n"
          + "]\n";

  public static final String VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_KEY =
      "cfl.vaccination.visit.encounterType.uuid.list";

  public static final String VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_DESCRIPTION =
      "The comma-separated list of Encounter Type UUIDs of the Encounters created when Vaccination visit occurs. "
          + "This creation of these Encounter triggers the next vaccination visit scheduling. Default value is "
          + "an UUID of Visit Note encounter type.";

  public static final String VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_DEFAULT_VALUE = "";

  public static final String VACCINATION_LISTENER_KEY = "cfl.vaccination.listener";

  public static final String VACCINATION_LISTENER_DESCRIPTION =
      "The name of listener which has to be responsible for "
          + "the scheduling of visits according to the program defined in parameter cfl.vaccines. Use "
          + "VaccinationEncounterListener to have visits scheduled after an Encounter for occurred visit is created, the "
          + "date-time of the encounter is used to calculate time (usually this is actual date of the visit) of the future"
          + " visits, see cfl.vaccination.visit.encounterType.uuid.list. Use UpdatingVisitListener to have visits "
          + "scheduled when there are no encounters created, the visit start date is going to be used to calculate the "
          + "times of the following visits.";

  /**
   * One of possible values for {@link #VACCINATION_LISTENER_KEY}. The name of {
   * VaccinationEncounterListener} listener.
   */
  public static final String VACCINATION_ENCOUNTER_LISTENER_NAME = "VaccinationEncounterListener";

  /**
   * One of possible values for {@link #VACCINATION_LISTENER_KEY}. The name of {
   * UpdatingVisitListener} listener.
   */
  public static final String VACCINATION_VISIT_LISTENER_NAME = "UpdatingVisitListener";

  public static final String CFL_PATIENT_SERVICE_BEAN_NAME = "cfl.patientService";

  public static final String VACCINATION_SERVICE_BEAN_NAME = "cfl.vaccinationService";

  public static final String GLOBAL_PROPERTY_HISTORY_SERVICE_BEAN_NAME =
      "cfl.globalPropertyHistoryService";

  public static final String MESSAGES_SCHEDULER_SERVICE_BEAN_NAME = "messages.schedulerService";

  public static final String UPDATE_REGIMEN_JOB_NAME = "Update Regimen Job";

  public static final String CFL_ADDRESS_DATA_SERVICE_BEAN_NAME = "cfl.addressService";

  public static final String CFL_VISIT_SERVICE_BEAN_NAME = "cfl.visitService";

  public static final String IS_LAST_DOSING_VISIT_ATTRIBUTE_NAME = "IsLastDosingVisit";

  public static final String IS_LAST_DOSING_VISIT_ATTR_TYPE_DATATYPE =
      FreeTextDatatype.class.getName();

  public static final String IS_LAST_DOSING_VISIT_ATTR_TYPE_DESCRIPTION =
      "Visit attribute type used to"
          + " determine if given visit is the last dosing visit. Possible values: true or false.";

  public static final String IS_LAST_DOSING_VISIT_ATTR_TYPE_UUID =
      "1e566e6d-108e-11ec-a36a-0242ac130002";

  public static final String MAIN_CONFIG = "biometric.api.config.main";

  public static final String REGIMEN_TAG_NAME = "regimen";

  public static final String EXTENDED_CONCEPT_SET_DAO_BEAN_NAME = "cfl.extendedConceptSetDAO";

  public static final String CREATION_PATIENT_TEMPLATES_AFTER_REGISTRATION_GP_KEY = "cfl.creationPatientTemplatesAfterRegistration";

  public static final String CREATION_PATIENT_TEMPLATES_AFTER_REGISTRATION_GP_DEFAULT_VALUE = "SMS";

  public static final String CREATION_PATIENT_TEMPLATES_AFTER_REGISTRATION_GP_DESCRIPTION =
      "Determines whether patient templates should be created after patient registration automatically. "
          + "If yes, channel type needs to be provided. If not, empty or false value is sufficient. Possible values: SMS or Call or false or empty value";

  public static final String PERSON_STATUS_ATTRIBUTE_TYPE_NAME = "Person status";

  public static final String PATIENT_FLAGS_OVERVIEW_IDENTIFIER_TYPE_FOR_SEARCH_GP_KEY = "cfl.patientFlagsOverviewIdentifierForSearch";

  public static final String PATIENT_FLAGS_OVERVIEW_IDENTIFIER_TYPE_FOR_SEARCH_GP_DEFAULT_VALUE = "OpenMRS ID";

  public static final String PATIENT_FLAGS_OVERVIEW_IDENTIFIER_TYPE_FOR_SEARCH_GP_DESCRIPTION =
      "Patient identifier type by which searching "
          + "for patients on flags overview page can be performed";

  public static final String CFL_UI_APP_PREFIX = "cflui.";

  public static final String REGISTER_PATIENT_APP_NAME = CFL_UI_APP_PREFIX.concat("registerPatient");

  public static final String REGISTER_CAREGIVER_APP_NAME = CFL_UI_APP_PREFIX.concat("registerCaregiver");

  public static final String FIND_PATIENT_APP_NAME = CFL_UI_APP_PREFIX.concat("findPatient");

  public static final String FIND_CAREGIVER_APP_NAME = CFL_UI_APP_PREFIX.concat("findCaregiver");
  private CFLConstants() {
  }
}
