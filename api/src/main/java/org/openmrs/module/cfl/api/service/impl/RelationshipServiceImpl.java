package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.RelationshipService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class RelationshipServiceImpl extends BaseOpenmrsService implements RelationshipService {

    private static final String VOIDED_REASON = "Voided during update relationships.";

    private PersonService personService;

    /**
     * Voids the legacy relationships for provided person and create a new one based on types from
     * {@link org.openmrs.module.cfl.api.domain.RelationshipDTO}.
     *
     * @param relationshipsTypes - list of {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} types
     * @param otherPeopleUUIDs   - list of {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} other people UUIDs
     * @param person             - related person
     * @return - list of new relationships creates based on {@link org.openmrs.module.cfl.api.domain.RelationshipDTO}
     */
    @Transactional
    @Override
    public List<Relationship> updatedRelationships(String[] relationshipsTypes, String[] otherPeopleUUIDs, Person person) {
        voidOldRelationships(person);
        return createNewRelationships(relationshipsTypes, otherPeopleUUIDs, person);
    }

    /**
     * Creates and saves a new relationships based on types from {@link org.openmrs.module.cfl.api.domain.RelationshipDTO}.
     *
     * @param relationshipsTypes - list of {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} types
     * @param otherPeopleUUIDs   - list of {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} other people UUIDs
     * @param person             - related person which already exists in the database
     * @return - list of new relationships creates based on {@link org.openmrs.module.cfl.api.domain.RelationshipDTO}
     */
    @Override
    public List<Relationship> createNewRelationships(String[] relationshipsTypes, String[] otherPeopleUUIDs, Person person) {
        List<Relationship> relationships = new ArrayList<Relationship>();
        for (int i = 0; i < relationshipsTypes.length; i++) {
            String newType = relationshipsTypes[i];
            String otherPersonUuid = (i < otherPeopleUUIDs.length) ? otherPeopleUUIDs[i] : null;
            if (StringUtils.isNotBlank(newType) && StringUtils.isNotBlank(otherPersonUuid)) {
                String relationshipTypeUUID = getRelationshipTypeUuid(newType);
                char relationshipDirection = getRelationshipDirection(newType);

                RelationshipType relationshipType = personService.getRelationshipTypeByUuid(relationshipTypeUUID);
                Person otherPerson = personService.getPersonByUuid(otherPersonUuid);
                if (relationshipType != null && otherPerson != null) {
                    Relationship relationship = createNewRelationship(person, relationshipDirection, relationshipType,
                            otherPerson);
                    saveRelationship(person, otherPerson, relationship);
                    relationships.add(relationship);
                }
            }
        }
        return relationships;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Creates a new relationship based based on provided direction.
     *
     * @param person - related person
     * @param relationshipDirection - direction of relationship
     * @param relationshipType - type of the relationship
     * @param otherPerson - other related person
     * @return - a new relationship
     */
    private Relationship createNewRelationship(Person person, char relationshipDirection, RelationshipType relationshipType,
            Person otherPerson) {
        Relationship relationship;
        if (relationshipDirection == 'A') {
            relationship = new Relationship(otherPerson, person, relationshipType);
        } else if (relationshipDirection == 'B') {
            relationship = new Relationship(person, otherPerson, relationshipType);
        } else {
            throw new APIException("Relationship direction not specified");
        }
        return relationship;
    }

    /**
     * Voids the actual relationships for provided person.
     *
     * @param person - a related person
     */
    private void voidOldRelationships(Person person) {
        for (Relationship relationship : personService.getRelationshipsByPerson(person)) {
            personService.voidRelationship(relationship, VOIDED_REASON);
            personService.saveRelationship(relationship);
        }
    }

    /**
     * Gets the relationships direction based on {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} type.
     * Last character reveals relationship direction (aIsToB or bIsToA)
     *
     * @param type - the value of {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} type
     * @return - relationship direction
     */
    private char getRelationshipDirection(String type) {
        return type.charAt(type.length() - 1);
    }

    /**
     * Gets the relationships type UUID based on {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} type.
     * Remove flag characters at the end (used for relationship direction)
     *
     * @param type - the value of {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} type
     * @return - relationship type UUID
     */
    private String getRelationshipTypeUuid(String type) {
        return type.substring(0, type.length() - 2);
    }

    /**
     * Verify if valid and saves new relationship in database.
     *
     * @param person - related person
     * @param otherPerson - related other person
     * @param relationship - relationship
     */
    private void saveRelationship(Person person, Person otherPerson, Relationship relationship) {
        if (person.getId() != null && person.getId().equals(otherPerson.getId())) {
            throw new APIException("Person A and Person B can't be the same");
        }
        personService.saveRelationship(relationship);
    }
}
