package org.openmrs.module.cfl.web.service.impl;

import org.apache.commons.lang.ObjectUtils;
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
import org.openmrs.module.cfl.api.service.RelationshipService;
import org.openmrs.module.cfl.web.dto.CFLRegistrationRelationshipDTO;
import org.openmrs.module.cfl.web.service.CFLRegistrationUiService;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The default implementation of {@link CFLRegistrationUiService}.
 */
@Component
public class CFLRegistrationUiServiceImpl implements CFLRegistrationUiService {
    private static final String UUID_PROPERTY = "uuid";
    private static final String RELATIONSHIPS_PROPERTY = "relationships";

    private PatientService patientService;
    private PersonService personService;
    private ConversionService conversionService;
    private RelationshipService relationshipService;

    @Autowired
    public void initDependencies(PatientService patientService, PersonService personService,
                                 ConversionService conversionService, RelationshipService relationshipService) {
        this.patientService = patientService;
        this.personService = personService;
        this.conversionService = conversionService;
        this.relationshipService = relationshipService;
    }

    @Override
    public Patient createOrUpdatePatient(final PropertyValues registrationProperties) {
        final Function<Patient, Void, RuntimeException> newPatientGetter = new Function<Patient, Void, RuntimeException>() {
            @Override
            public Patient call(Void ignore) throws RuntimeException {
                return new Patient();
            }
        };

        final Function<Patient, String, RuntimeException> existingPatientGetter =
                new Function<Patient, String, RuntimeException>() {
                    @Override
                    public Patient call(String uuid) throws RuntimeException {
                        return patientService.getPatientByUuid(uuid);
                    }
                };

        final Patient patient = getOrCreateActor(registrationProperties, newPatientGetter, existingPatientGetter);
        bindProperties(patient, registrationProperties);
        addPersonName(patient, registrationProperties);
        addPersonAddress(patient, registrationProperties);
        addPersonAttributes(patient, registrationProperties);
        addPatientIdentifiers(patient, registrationProperties);
        return patient;
    }

    @Override
    public Person createOrUpdatePerson(PropertyValues registrationProperties) {
        final Function<Person, Void, RuntimeException> newPersonGetter = new Function<Person, Void, RuntimeException>() {
            @Override
            public Person call(Void ignore) throws RuntimeException {
                return new Person();
            }
        };

        final Function<Person, String, RuntimeException> existingPersonGetter =
                new Function<Person, String, RuntimeException>() {
                    @Override
                    public Person call(String uuid) throws RuntimeException {
                        return personService.getPersonByUuid(uuid);
                    }
                };

        final Person person = getOrCreateActor(registrationProperties, newPersonGetter, existingPersonGetter);
        bindProperties(person, registrationProperties);
        addPersonName(person, registrationProperties);
        addPersonAddress(person, registrationProperties);
        addPersonAttributes(person, registrationProperties);
        return person;
    }

    @Override
    public List<Relationship> parseRelationships(final PropertyValues registrationProperties) {
        final PropertyValue relationshipsRawValue = registrationProperties.getPropertyValue(RELATIONSHIPS_PROPERTY);

        if (relationshipsRawValue == null || relationshipsRawValue.getValue() == null) {
            return Collections.emptyList();
        }

        if (!(relationshipsRawValue.getValue() instanceof Iterable)) {
            throw new APIException("Invalid data " + relationshipsRawValue.getValue() + " provided for property: " +
                    RELATIONSHIPS_PROPERTY);
        }

        final List<Relationship> patientRelationships = new ArrayList<Relationship>();

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
    public void updateRelationships(final Person person, final List<Relationship> newRelationships) {
        updateRelationshipPerson(person, newRelationships);

        final List<RelationshipDTO> newRelationshipDTOs = buildRelationshipDTOs(person, newRelationships);

        relationshipService.updatedRelationships(newRelationshipDTOs, person);
    }

    private <Actor extends Person> Actor getOrCreateActor(PropertyValues registrationProperties,
                                                          Function<Actor, Void, RuntimeException> newActorGetter,
                                                          Function<Actor, String, RuntimeException> existingActorGetter) {
        final PropertyValue uuidProperty = registrationProperties.getPropertyValue(UUID_PROPERTY);

        final Actor actor;
        if (uuidProperty == null) {
            actor = newActorGetter.call(null);
        } else {
            final String uuid = ObjectUtils.toString(
                    uuidProperty.isConverted() ? uuidProperty.getConvertedValue() : uuidProperty.getValue());

            actor = existingActorGetter.call(uuid);

            if (actor == null) {
                throw new APIException("Could not find Actor (Patient or Person) with UUID: " + uuid);
            }
        }

        return actor;
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
            final Object attributeValueRaw =
                    attributePropertyValue.isConverted() ? attributePropertyValue.getConvertedValue() :
                            attributePropertyValue.getValue();
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

            final PropertyValue identifierPropertyValue =
                    registrationProperties.getPropertyValue(patientIdentifierType.getName());
            final Object identifierValueRaw =
                    identifierPropertyValue.isConverted() ? identifierPropertyValue.getConvertedValue() :
                            identifierPropertyValue.getValue();
            final String identifierValue = ObjectUtils.toString(identifierValueRaw, null);

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
        final List<RelationshipDTO> result = new ArrayList<RelationshipDTO>();

        for (final Relationship relationship : relationships) {
            result.add(relationshipService.buildRelationshipDTO(relatedTo, relationship));
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
    private interface Function<R, A, E extends Throwable> {
        R call(A argument) throws E;
    }
}
