package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Provider;
import org.openmrs.RelationshipType;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfldistribution.CfldistributionGlobalParameterConstants;
import org.openmrs.module.cfldistribution.api.activator.ModuleActivatorStep;

import java.util.Arrays;
import java.util.List;

/**
 * Cleans
 * <p>
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 * </p>
 */
public class DataCleanupActivatorStep implements ModuleActivatorStep {
    private static final String LOCATION_UUID_PROPERTY_NAME = "locationUuid";
    private static final String UNKNOWN_LOCATION_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
    private static final String CFL_CLINIC_LOCATION_NAME = "CFL Clinic";
    private static final String LOGIN_LOCATION_TAG_NAME = "Login Location";
    private static final String VISIT_LOCATION_TAG_NAME = "Visit Location";
    private static final String USERS_TO_REMOVE = "clerk,nurse,doctor,sysadmin,scheduler";
    private static final String COMMA_SEPARATOR = ",";
    private static final String RELATIONSHIP_TYPES_UUIDS_TO_REMOVE = "8d919b58-c2cc-11de-8d13-0010c6dffd0f," +
            "8d91a01c-c2cc-11de-8d13-0010c6dffd0f,8d91a210-c2cc-11de-8d13-0010c6dffd0f," +
            "8d91a3dc-c2cc-11de-8d13-0010c6dffd0f,2a5f4ff4-a179-4b8a-aa4c-40f71956ebbc";
    private static final String PERSON_ATTRIBUTE_TYPES_TO_REMOVE =
            "Race,Birthplace,Citizenship,Mother's Name,Civil Status,Health District,Health Center,Test Patient";
    private static final String PATIENT_IDENTIFIER_TYPES_TO_REMOVE =
            "OpenMRS Identification Number,Old Identification Number,OpenEMPI ID";
    private static final String PROVIDERS_TO_REMOVE = "clerk,nurse,doctor";
    private static final String LOCATION_UUIDS_TO_REMOVE = "7f65d926-57d6-4402-ae10-a5b3bcbf7986," +
            "7fdfa2cb-bc95-405a-88c6-32b7673c0453,2131aff8-2e2a-480a-b7ab-4ac53250262b," +
            "6351fcf4-e311-4a19-90f9-35667d99a8af,b1a8b05e-3542-4037-bbd3-998ee9c40574," +
            "58c57d25-8d39-41ab-8422-108a0c277d98,aff27d58-a15c-49a6-9beb-d30dcfc0c66e";

    private final Log LOGGER = LogFactory.getLog(DataCleanupActivatorStep.class);

    @Override
    public int getOrder() {
        return 50;
    }

    @Override
    public void startup(Log log) {
        if (!Boolean.parseBoolean(Context
                .getAdministrationService()
                .getGlobalProperty(CfldistributionGlobalParameterConstants.CFL_DISTRO_BOOTSTRAPPED_KEY))) {
            removeUnnecessaryData();
            createAndUpdateNecessaryData();
            markInitialDataCleanupAsDone();
        } else {
            LOGGER.info("Initial data cleanup has already been performed");
        }
    }

    private void removeUnnecessaryData() {
        removeUnnecessaryUsers();
        removeUnnecessaryPatientIdentifierTypes();
        removeUnnecessaryRelationshipTypes();
        removeUnnecessaryPersonAttributeTypes();
        removeUnnecessaryProviders();
        removeUnnecessaryLocations();
        fixInvalidConceptReferenceTerm();
    }

    private void createAndUpdateNecessaryData() {
        updateUserProperty(CFLConstants.ADMIN_USER_NAME, LOCATION_UUID_PROPERTY_NAME, UNKNOWN_LOCATION_UUID);
        updateLocationName(UNKNOWN_LOCATION_UUID, CFL_CLINIC_LOCATION_NAME);
        addNewLocationTagToLocation(UNKNOWN_LOCATION_UUID, LOGIN_LOCATION_TAG_NAME);
        addNewLocationTagToLocation(UNKNOWN_LOCATION_UUID, VISIT_LOCATION_TAG_NAME);
    }

    private void markInitialDataCleanupAsDone() {
        Context
                .getAdministrationService()
                .setGlobalProperty(CfldistributionGlobalParameterConstants.CFL_DISTRO_BOOTSTRAPPED_KEY, Boolean.TRUE.toString());
    }

    private void removeUnnecessaryUsers() {
        UserService userService = Context.getUserService();
        List<String> usersToRemove = getSplitPropertiesBySeparator(USERS_TO_REMOVE);
        for (User user : userService.getAllUsers()) {
            if (usersToRemove.contains(user.getUsername())) {
                userService.purgeUser(user);
            }
        }
    }

    private void removeUnnecessaryPatientIdentifierTypes() {
        PatientService patientService = Context.getPatientService();
        List<String> patientIdentifierTypesToRemove = getSplitPropertiesBySeparator(PATIENT_IDENTIFIER_TYPES_TO_REMOVE);
        for (PatientIdentifierType patientIdentifierType : patientService.getAllPatientIdentifierTypes()) {
            if (patientIdentifierTypesToRemove.contains(patientIdentifierType.getName())) {
                patientService.purgePatientIdentifierType(patientIdentifierType);
            }
        }
    }

    private void removeUnnecessaryRelationshipTypes() {
        PersonService personService = Context.getPersonService();
        List<String> relationshipTypesUuidsToRemove = getSplitPropertiesBySeparator(RELATIONSHIP_TYPES_UUIDS_TO_REMOVE);
        for (RelationshipType relationshipType : personService.getAllRelationshipTypes(true)) {
            if (relationshipTypesUuidsToRemove.contains(relationshipType.getUuid())) {
                personService.purgeRelationshipType(relationshipType);
            }
        }
    }

    private void removeUnnecessaryPersonAttributeTypes() {
        PersonService personService = Context.getPersonService();
        List<String> personAttributeTypesToRemove = getSplitPropertiesBySeparator(PERSON_ATTRIBUTE_TYPES_TO_REMOVE);
        for (PersonAttributeType personAttributeType : personService.getAllPersonAttributeTypes()) {
            if (personAttributeTypesToRemove.contains(personAttributeType.getName())) {
                personService.purgePersonAttributeType(personAttributeType);
            }
        }
    }

    private void removeUnnecessaryProviders() {
        ProviderService providerService = Context.getProviderService();
        List<String> providersToRemove = getSplitPropertiesBySeparator(PROVIDERS_TO_REMOVE);
        for (Provider provider : providerService.getAllProviders()) {
            if (providersToRemove.contains(provider.getIdentifier())) {
                providerService.purgeProvider(provider);
            }
        }
    }

    private void removeUnnecessaryLocations() {
        LocationService locationService = Context.getLocationService();
        List<String> locationsUuidsToRemove = getSplitPropertiesBySeparator(LOCATION_UUIDS_TO_REMOVE);
        for (Location location : locationService.getAllLocations()) {
            if (locationsUuidsToRemove.contains(location.getUuid()) && location.getParentLocation() == null) {
                locationService.purgeLocation(location);
            }
        }
    }

    private void updateUserProperty(String username, String property, String value) {
        UserService userService = Context.getUserService();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            user.setUserProperty(property, value);
        } else {
            LOGGER.warn(String.format("User with username: %s not found", username));
        }

    }

    private void updateLocationName(String locationUuid, String newName) {
        Location location = Context.getLocationService().getLocationByUuid(locationUuid);
        if (location != null) {
            location.setName(newName);
        } else {
            LOGGER.warn(String.format("Location with uuid: %s not found", locationUuid));
        }

    }

    private void addNewLocationTagToLocation(String locationUuid, String locationTagName) {
        LocationService locationService = Context.getLocationService();
        Location location = locationService.getLocationByUuid(locationUuid);
        if (location != null) {
            location.addTag(locationService.getLocationTagByName(locationTagName));
        } else {
            LOGGER.warn(String.format("Location with uuid: %s not found", locationUuid));
        }
    }

    private List<String> getSplitPropertiesBySeparator(String property) {
        return Arrays.asList(property.split(COMMA_SEPARATOR));
    }

    /**
     * Fix the invalid ConceptReferenceTerm added by openmrs-module-referencemetadata/Covid-19_Concepts-1.xml
     */
    private void fixInvalidConceptReferenceTerm() {
        final String validConfirmedConceptTermUuid = "a69dafbf-5b89-3c59-a7e2-8c0477ca4bc0";
        final String invalidConfirmedConceptTermUuid = "4ebd268b-a4fe-369d-82ff-353a0a922c20";
        final String invalidConceptFromCovidRefMetadataUuid = "165792AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        final ConceptService conceptService = Context.getConceptService();

        final ConceptReferenceTerm invalidTerm =
                conceptService.getConceptReferenceTermByUuid(invalidConfirmedConceptTermUuid);
        final Concept conceptWithInvalidTerm = conceptService.getConceptByUuid(invalidConceptFromCovidRefMetadataUuid);
        final ConceptReferenceTerm validTerm = conceptService.getConceptReferenceTermByUuid(validConfirmedConceptTermUuid);

        if (invalidTerm == null || conceptWithInvalidTerm == null) {
            LOGGER.info(new StringBuilder("Invalid Concept data [term: ")
                    .append(invalidConfirmedConceptTermUuid)
                    .append(", concept:")
                    .append(invalidConceptFromCovidRefMetadataUuid)
                    .append(")] from openmrs-module-referencemetadata/Covid-19_Concepts-1.xml ware not found.")
                    .toString());
        } else if (validTerm == null) {
            LOGGER.error(new StringBuilder("Concept [")
                    .append(validConfirmedConceptTermUuid)
                    .append("] used to fix openmrs-module-referencemetadata/Covid-19_Concepts-1.xml was NOT found! ")
                    .append("The system may be unstable!")
                    .toString());
        } else {
            for (final ConceptMap conceptMap : conceptWithInvalidTerm.getConceptMappings()) {
                if (conceptMap.getConceptReferenceTerm().equals(invalidTerm)) {
                    conceptMap.setConceptReferenceTerm(validTerm);
                }
            }

            conceptService.purgeConceptReferenceTerm(invalidTerm);
        }
    }
}
