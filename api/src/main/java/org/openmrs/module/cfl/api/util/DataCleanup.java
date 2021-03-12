package org.openmrs.module.cfl.api.util;

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

import java.util.Arrays;
import java.util.List;

public final class DataCleanup {

    private static final Log LOGGER = LogFactory.getLog(DataCleanup.class);

    private DataCleanup() {
    }

    public static void cleanUpUnnecessaryData() {
        if (!Boolean.parseBoolean(
                Context.getAdministrationService().getGlobalProperty(CFLConstants.CFL_DISTRO_BOOTSTRAPPED_KEY))) {
            removeUnnecessaryData();
            createAndUpdateNecessaryData();
            markInitialDataCleanupAsDone();
        } else {
            LOGGER.info("Initial data cleanup has already been performed");
        }
    }

    private static void removeUnnecessaryData() {
        removeUnnecessaryUsers();
        removeUnnecessaryPatientIdentifierTypes();
        removeUnnecessaryRelationshipTypes();
        removeUnnecessaryPersonAttributeTypes();
        removeUnnecessaryProviders();
        removeUnnecessaryLocations();
        fixInvalidConceptReferenceTerm();
    }

    private static void createAndUpdateNecessaryData() {
        updateUserProperty(CFLConstants.ADMIN_USER_NAME, CFLConstants.LOCATION_UUID_PROPERTY_NAME,
                CFLConstants.UNKNOWN_LOCATION_UUID);
        updateLocationName(CFLConstants.UNKNOWN_LOCATION_UUID, CFLConstants.CFL_CLINIC_LOCATION_NAME);
        addNewLocationTagToLocation(CFLConstants.UNKNOWN_LOCATION_UUID, CFLConstants.LOGIN_LOCATION_TAG_NAME);
        addNewLocationTagToLocation(CFLConstants.UNKNOWN_LOCATION_UUID, CFLConstants.VISIT_LOCATION_TAG_NAME);
    }

    private static void markInitialDataCleanupAsDone() {
        Context.getAdministrationService().setGlobalProperty(CFLConstants.CFL_DISTRO_BOOTSTRAPPED_KEY, CFLConstants.TRUE);
    }

    private static void removeUnnecessaryUsers() {
        UserService userService = Context.getUserService();
        List<String> usersToRemove =
                getSplitPropertiesBySeparator(CFLConstants.USERS_TO_REMOVE, CFLConstants.COMMA_SEPARATOR);
        for (User user : userService.getAllUsers()) {
            if (usersToRemove.contains(user.getUsername())) {
                userService.purgeUser(user);
            }
        }
    }

    private static void removeUnnecessaryPatientIdentifierTypes() {
        PatientService patientService = Context.getPatientService();
        List<String> patientIdentifierTypesToRemove =
                getSplitPropertiesBySeparator(CFLConstants.PATIENT_IDENTIFIER_TYPES_TO_REMOVE, CFLConstants.COMMA_SEPARATOR);
        for (PatientIdentifierType patientIdentifierType : patientService.getAllPatientIdentifierTypes()) {
            if (patientIdentifierTypesToRemove.contains(patientIdentifierType.getName())) {
                patientService.purgePatientIdentifierType(patientIdentifierType);
            }
        }
    }

    private static void removeUnnecessaryRelationshipTypes() {
        PersonService personService = Context.getPersonService();
        List<String> relationshipTypesUuidsToRemove =
                getSplitPropertiesBySeparator(CFLConstants.RELATIONSHIP_TYPES_UUIDS_TO_REMOVE, CFLConstants.COMMA_SEPARATOR);
        for (RelationshipType relationshipType : personService.getAllRelationshipTypes(true)) {
            if (relationshipTypesUuidsToRemove.contains(relationshipType.getUuid())) {
                personService.purgeRelationshipType(relationshipType);
            }
        }
    }

    private static void removeUnnecessaryPersonAttributeTypes() {
        PersonService personService = Context.getPersonService();
        List<String> personAttributeTypesToRemove =
                getSplitPropertiesBySeparator(CFLConstants.PERSON_ATTRIBUTE_TYPES_TO_REMOVE, CFLConstants.COMMA_SEPARATOR);
        for (PersonAttributeType personAttributeType : personService.getAllPersonAttributeTypes()) {
            if (personAttributeTypesToRemove.contains(personAttributeType.getName())) {
                personService.purgePersonAttributeType(personAttributeType);
            }
        }
    }

    private static void removeUnnecessaryProviders() {
        ProviderService providerService = Context.getProviderService();
        List<String> providersToRemove =
                getSplitPropertiesBySeparator(CFLConstants.PROVIDERS_TO_REMOVE, CFLConstants.COMMA_SEPARATOR);
        for (Provider provider : providerService.getAllProviders()) {
            if (providersToRemove.contains(provider.getIdentifier())) {
                providerService.purgeProvider(provider);
            }
        }
    }

    private static void removeUnnecessaryLocations() {
        LocationService locationService = Context.getLocationService();
        List<String> locationsUuidsToRemove =
                getSplitPropertiesBySeparator(CFLConstants.LOCATION_UUIDS_TO_REMOVE, CFLConstants.COMMA_SEPARATOR);
        for (Location location : locationService.getAllLocations()) {
            if (locationsUuidsToRemove.contains(location.getUuid()) && location.getParentLocation() == null) {
                locationService.purgeLocation(location);
            }
        }
    }

    private static void updateUserProperty(String username, String property, String value) {
        UserService userService = Context.getUserService();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            user.setUserProperty(property, value);
        } else {
            LOGGER.warn(String.format("User with username: %s not found", username));
        }

    }

    private static void updateLocationName(String locationUuid, String newName) {
        Location location = Context.getLocationService().getLocationByUuid(locationUuid);
        if (location != null) {
            location.setName(newName);
        } else {
            LOGGER.warn(String.format("Location with uuid: %s not found", locationUuid));
        }

    }

    private static void addNewLocationTagToLocation(String locationUuid, String locationTagName) {
        LocationService locationService = Context.getLocationService();
        Location location = locationService.getLocationByUuid(locationUuid);
        if (location != null) {
            location.addTag(locationService.getLocationTagByName(locationTagName));
        } else {
            LOGGER.warn(String.format("Location with uuid: %s not found", locationUuid));
        }
    }

    private static List<String> getSplitPropertiesBySeparator(String property, String separator) {
        return Arrays.asList(property.split(separator));
    }

    /**
     * Fix the invalid ConceptReferenceTerm added by openmrs-module-referencemetadata/Covid-19_Concepts-1.xml
     */
    private static void fixInvalidConceptReferenceTerm() {
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
