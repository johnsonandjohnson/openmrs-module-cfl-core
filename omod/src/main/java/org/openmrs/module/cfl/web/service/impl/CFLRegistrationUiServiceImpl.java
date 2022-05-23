/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.web.service.impl;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Attributable;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.dto.RelationshipDTO;
import org.openmrs.module.cfl.api.registration.person.action.AfterPersonCreatedAction;
import org.openmrs.module.cfl.api.service.RelationshipService;
import org.openmrs.module.cfl.web.dto.CFLRegistrationRelationshipDTO;
import org.openmrs.module.cfl.web.service.CFLRegistrationUiService;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.openmrs.module.registrationcore.RegistrationCoreUtil.calculateBirthdateFromAge;

/**
 * The default implementation of {@link CFLRegistrationUiService}.
 * <p>
 * This bean is configured in: resources/webModuleApplicationContext.xml
 * </p>
 * <p>
 * Located in omod because `org.openmrs.module.webservices.rest.web.ConversionUtil` is in OMOD too.
 * </p>
 */
public class CFLRegistrationUiServiceImpl implements CFLRegistrationUiService {
    private static final String UUID_PROPERTY = "uuid";
    private static final String RELATIONSHIPS_PROPERTY = "relationships";
    private static final String BIRTHDATE_YEARS_PROPERTY = "birthdateYears";
    private static final String BIRTHDATE_MONTHS_PROPERTY = "birthdateMonths";

    private PatientService patientService;
    private PersonService personService;
    private ConversionService conversionService;
    private RelationshipService relationshipService;

    @Override
    @Transactional
    public Patient createOrUpdatePatient(final PropertyValues registrationProperties) {
        final Patient patient =
                getOrCreateActor(registrationProperties, Patient::new, uuid -> patientService.getPatientByUuid(uuid));
        bindProperties(patient, registrationProperties);
        handleBirthdateEstimate(patient, registrationProperties);
        addPersonName(patient, registrationProperties);
        addPersonAddress(patient, registrationProperties);
        addPersonAttributes(patient, registrationProperties);
        addPatientIdentifiers(patient, registrationProperties);
        return patient;
    }

    @Override
    @Transactional
    public Person createOrUpdatePerson(PropertyValues registrationProperties) {
        final Person person =
                getOrCreateActor(registrationProperties, Person::new, uuid -> personService.getPersonByUuid(uuid));
        bindProperties(person, registrationProperties);
        addPersonName(person, registrationProperties);
        addPersonAddress(person, registrationProperties);
        addPersonAttributes(person, registrationProperties);

        if (isNewActor(registrationProperties)) {
            runAllAfterPersonCreatedActions(person, registrationProperties);
        }

        return person;
    }

    @Override
    @Transactional
    public List<Relationship> parseRelationships(final PropertyValues registrationProperties) {
        final PropertyValue relationshipsRawValue = registrationProperties.getPropertyValue(RELATIONSHIPS_PROPERTY);

        if (relationshipsRawValue == null || relationshipsRawValue.getValue() == null) {
            return Collections.emptyList();
        }

        if (!(relationshipsRawValue.getValue() instanceof Iterable)) {
            throw new APIException("Invalid data " + relationshipsRawValue.getValue() + " provided for property: " +
                    RELATIONSHIPS_PROPERTY);
        }

        final List<Relationship> patientRelationships = new ArrayList<>();

        for (final Object relationshipRaw : (Iterable) relationshipsRawValue.getValue()) {
            if (!(relationshipRaw instanceof Map)) {
                throw new APIException(
                        "Invalid data " + relationshipRaw + " provided for an item of property: " + RELATIONSHIPS_PROPERTY);
            }

            final PropertyValues relationshipPropertyValues = new MutablePropertyValues((Map<?, ?>) relationshipRaw);
            final Relationship relationship = parseRelationship(relationshipPropertyValues);
            patientRelationships.add(relationship);
        }

        return patientRelationships;
    }

    @Override
    @Transactional
    public void updateRelationships(final Person person, final List<Relationship> newRelationships) {
        updateRelationshipPerson(person, newRelationships);

        final List<RelationshipDTO> newRelationshipDTOs = buildRelationshipDTOs(person, newRelationships);

        relationshipService.updatedRelationships(newRelationshipDTOs, person);
    }

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public void setRelationshipService(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    private <Actor extends Person> Actor getOrCreateActor(PropertyValues registrationProperties,
                                                          Supplier<Actor, RuntimeException> newActorGetter,
                                                          Function<Actor, String, RuntimeException> existingActorGetter) {
        final Actor actor;
        if (isNewActor(registrationProperties)) {
            actor = newActorGetter.get();
        } else {
            final PropertyValue uuidProperty = registrationProperties.getPropertyValue(UUID_PROPERTY);
            final String uuid = ObjectUtils.toString(
                    uuidProperty.isConverted() ? uuidProperty.getConvertedValue() : uuidProperty.getValue());

            actor = existingActorGetter.call(uuid);

            if (actor == null) {
                throw new APIException("Could not find Actor (Patient or Person) with UUID: " + uuid);
            }
        }

        return actor;
    }

    private boolean isNewActor(PropertyValues registrationProperties) {
        return !registrationProperties.contains(UUID_PROPERTY);
    }

    private void bindProperties(final Object target, final PropertyValues properties) {
        final DataBinder patientBinder = new DataBinder(target);
        patientBinder.setConversionService(conversionService);
        patientBinder.bind(properties);

        final BindingResult bindingResult = patientBinder.getBindingResult();

        if (bindingResult.hasErrors()) {
            throw new APIException("Invalid data provided for object: " + target.getClass().getSimpleName() + ", errors: " +
                    bindingResult.getAllErrors());
        }
    }

    private void handleBirthdateEstimate(final Patient patient, final PropertyValues registrationProperties) {
        if ((registrationProperties.contains(BIRTHDATE_YEARS_PROPERTY) ||
                registrationProperties.contains(BIRTHDATE_MONTHS_PROPERTY))) {

            final Integer birthdateYears =
                    conversionService.convert(valueOf(registrationProperties.getPropertyValue(BIRTHDATE_YEARS_PROPERTY)),
                            Integer.class);

            final Integer birthdateMonths =
                    conversionService.convert(valueOf(registrationProperties.getPropertyValue(BIRTHDATE_MONTHS_PROPERTY)),
                            Integer.class);

            patient.setBirthdateEstimated(true);
            patient.setBirthdate(calculateBirthdateFromAge(birthdateYears, birthdateMonths, null, null));
        }
    }

    private void addPersonName(final Person person, final PropertyValues registrationProperties) {
        final PersonName personName = person.getPersonName() != null ? person.getPersonName() : new PersonName();
        bindProperties(personName, registrationProperties);
        person.addName(personName);
    }

    private void addPersonAddress(final Person person, final PropertyValues registrationProperties) {
        final PersonAddress personAddress =
                person.getPersonAddress() != null ? person.getPersonAddress() : new PersonAddress();
        bindProperties(personAddress, registrationProperties);
        person.addAddress(personAddress);
    }

    private void addPersonAttributes(final Person person, final PropertyValues registrationProperties) {
        final List<PersonAttributeType> personAttributeTypes = personService.getAllPersonAttributeTypes(false);
        for (final PersonAttributeType personAttributeType : personAttributeTypes) {
            if (!registrationProperties.contains(personAttributeType.getName())) {
                continue;
            }

            final PropertyValue attributePropertyValue =
                    registrationProperties.getPropertyValue(personAttributeType.getName());
            final Object attributeValueRaw = valueOf(attributePropertyValue);
            final String attributeValue = getSafeAttributeValue(personAttributeType, attributeValueRaw);

            final PersonAttribute attribute = new PersonAttribute();
            attribute.setAttributeType(personAttributeType);
            attribute.setValue(attributeValue);
            person.addAttribute(attribute);
        }
    }

    private void addPatientIdentifiers(final Patient patient, final PropertyValues registrationProperties) {
        final List<PatientIdentifierType> patientIdentifierTypes = patientService.getAllPatientIdentifierTypes(false);
        for (final PatientIdentifierType patientIdentifierType : patientIdentifierTypes) {
            if (!registrationProperties.contains(patientIdentifierType.getName())) {
                continue;
            }

            addPatientIdentifier(patient, registrationProperties, patientIdentifierType);
        }
    }

    private void addPatientIdentifier(final Patient patient, final PropertyValues registrationProperties,
                                      final PatientIdentifierType patientIdentifierType) {
        final PropertyValue identifierPropertyValue =
                registrationProperties.getPropertyValue(patientIdentifierType.getName());
        final Object identifierValueRaw = valueOf(identifierPropertyValue);
        final String identifierValue = ObjectUtils.toString(identifierValueRaw, null);

        if (StringUtils.isBlank(identifierValue)) {
            voidPatientIdentifiers(patient, patientIdentifierType);
            return;
        }

        final PatientIdentifier newIdentifier = new PatientIdentifier();
        newIdentifier.setIdentifier(identifierValue);
        newIdentifier.setIdentifierType(patientIdentifierType);

        final PatientIdentifier currentPatientIdentifier = patient.getPatientIdentifier(patientIdentifierType);

        if (currentPatientIdentifier != null) {
            if (!currentPatientIdentifier.equalsContent(newIdentifier)) {
                voidPatientIdentifiers(patient, patientIdentifierType);
                patient.addIdentifier(newIdentifier);
            }
        } else {
            patient.addIdentifier(newIdentifier);
        }
    }

    private void voidPatientIdentifiers(final Patient patient, final PatientIdentifierType patientIdentifierType) {
        // Void all identifiers of given type to make sure of DB consistency - only one non-voided identifier per type
        // should exist
        for (PatientIdentifier oldIdentifier : patient.getPatientIdentifiers(patientIdentifierType)) {
            oldIdentifier.setVoided(true);
            oldIdentifier.setVoidedBy(Context.getAuthenticatedUser());
            oldIdentifier.setDateVoided(new Date());
            oldIdentifier.setVoidReason("Value changed");
        }
    }

    private String getSafeAttributeValue(final PersonAttributeType personAttributeType, final Object rawValue) {
        String result = null;

        if (personAttributeType.getFormat() != null) {
            try {
                final Class<?> clazz = Context.loadClass(personAttributeType.getFormat());
                if (Attributable.class.isAssignableFrom(clazz)) {
                    final Attributable instance = (Attributable) ConversionUtil.convert(rawValue, clazz);
                    if (instance != null) {
                        result = instance.serialize();
                    }
                }
            } catch (ClassNotFoundException cnf) {
                throw new APIException("Invalid format: " + personAttributeType.getFormat() + " in Person Attribute Type: " +
                        personAttributeType.getName(), cnf);
            }
        }

        if (result == null) {
            result = ObjectUtils.toString(rawValue, null);
        }

        return result;
    }

    private Relationship parseRelationship(final PropertyValues relationshipPropertyValues) {
        final CFLRegistrationRelationshipDTO registrationRelationshipDTO = new CFLRegistrationRelationshipDTO();
        bindProperties(registrationRelationshipDTO, relationshipPropertyValues);

        final RelationshipType relationshipType = getRelationshipType(registrationRelationshipDTO);
        final Person otherPerson = getOtherPerson(registrationRelationshipDTO);

        final Relationship result;

        switch (registrationRelationshipDTO.getRelationshipDirection()) {
            case A:
                result = new Relationship(otherPerson, null, relationshipType);
                break;
            case B:
                result = new Relationship(null, otherPerson, relationshipType);
                break;
            case UNKNOWN:
            default:
                throw new APIException("Could not determine the Relationship's direction from Type UUID: " +
                        registrationRelationshipDTO.getRelationshipTypeUuid());
        }

        return result;
    }

    private RelationshipType getRelationshipType(final CFLRegistrationRelationshipDTO registrationRelationshipDTO) {
        final String relationshipTypeByUuid = registrationRelationshipDTO.getRelationshipTypeUuid();
        final RelationshipType relationshipType = personService.getRelationshipTypeByUuid(relationshipTypeByUuid);

        if (relationshipType == null) {
            throw new APIException("Could not find Relationship Type for UUID: " + relationshipTypeByUuid);
        }

        return relationshipType;
    }

    private Person getOtherPerson(final CFLRegistrationRelationshipDTO registrationRelationshipDTO) {
        final String personUuid = registrationRelationshipDTO.getOtherPersonUuid();
        final Person otherPerson = personService.getPersonByUuid(personUuid);

        if (otherPerson == null) {
            throw new APIException("Could not find Person for UUID: " + personUuid);
        }

        return otherPerson;
    }

    private void updateRelationshipPerson(final Person person, final List<Relationship> relationships) {
        for (final Relationship relationship : relationships) {
            if (relationship.getPersonA() == null) {
                relationship.setPersonA(person);
            } else if (relationship.getPersonB() == null) {
                relationship.setPersonB(person);
            }
        }
    }

    private List<RelationshipDTO> buildRelationshipDTOs(final Person relatedTo, final Iterable<Relationship> relationships) {
        final List<RelationshipDTO> result = new ArrayList<>();

        for (final Relationship relationship : relationships) {
            result.add(relationshipService.buildRelationshipDTO(relatedTo, relationship));
        }

        return result;
    }

    private Object valueOf(final PropertyValue propertyValue) {
        if (propertyValue == null) {
            return null;
        }

        return propertyValue.isConverted() ? propertyValue.getConvertedValue() : propertyValue.getValue();
    }

    private void runAllAfterPersonCreatedActions(final Person person, final PropertyValues registrationProperties) {
        final Map<String, String[]> submittedParameters = Arrays
                .stream(registrationProperties.getPropertyValues())
                .collect(Collectors.toMap(PropertyValue::getName, this::toSubmittedParameterValue));

        Context
                .getRegisteredComponents(AfterPersonCreatedAction.class)
                .forEach(action -> action.afterCreated(person, submittedParameters));
    }

    private String[] toSubmittedParameterValue(PropertyValue propertyValue) {
        final Object value = propertyValue.getValue();
        final String[] result;

        if (value instanceof Collection) {
            result = ((Collection<?>) value).stream().map(Object::toString).toArray(String[]::new);
        } else if (value != null && value.getClass().isArray()) {
            result = Arrays.stream((Object[]) value).map(Object::toString).toArray(String[]::new);
        } else if (value != null) {
            result = new String[] {value.toString()};
        } else {
            result = new String[0];
        }

        return result;
    }

    /**
     * Internal utility class.
     *
     * @param <R> the type of result
     * @param <A> the type of argument
     * @param <E> the type of exception thrown
     */
    @FunctionalInterface
    private interface Function<R, A, E extends Throwable> {
        R call(A argument) throws E;
    }

    /**
     * Internal utility class.
     *
     * @param <R> the type of result
     * @param <E> the type of exception thrown
     */
    @FunctionalInterface
    private interface Supplier<R, E extends Throwable> {
        R get() throws E;
    }
}
