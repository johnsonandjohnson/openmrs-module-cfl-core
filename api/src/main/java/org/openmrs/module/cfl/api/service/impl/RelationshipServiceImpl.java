package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.dto.RelationshipDTO;
import org.openmrs.module.cfl.api.service.RelationshipService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class RelationshipServiceImpl extends BaseOpenmrsService implements RelationshipService {

    private static final String VOIDED_REASON = "Voided during update relationships.";
    private static final String PERSON_A_SUFFIX = "-A";
    private static final String PERSON_B_SUFFIX = "-B";

    private PersonService personService;

    /**
     * @see RelationshipService#updatedRelationships(List, Person)
     */
    @Transactional
    @Override
    public List<Relationship> updatedRelationships(List<RelationshipDTO> receivedRelationships, Person person) {
        List<RelationshipDTO> newRelationshipToCreate = voidOldAndExtractNewRelationship(receivedRelationships, person);
        return createNewRelationships(newRelationshipToCreate, person);
    }

    /**
     * @see RelationshipService#createNewRelationships(List, Person)
     */
    @Override
    public List<Relationship> createNewRelationships(List<RelationshipDTO> currentRelationships, Person person) {
        for (RelationshipDTO relationshipDTO : currentRelationships) {
            String newType = relationshipDTO.getType();
            String otherPersonUuid = relationshipDTO.getUuid();
            if (StringUtils.isNotBlank(newType) && StringUtils.isNotBlank(otherPersonUuid)) {
                String relationshipTypeUUID = getRelationshipTypeUuid(newType);
                char relationshipDirection = getRelationshipDirection(newType);
                RelationshipType relationshipType = personService.getRelationshipTypeByUuid(relationshipTypeUUID);
                Person otherPerson = personService.getPersonByUuid(otherPersonUuid);
                if (relationshipType != null && otherPerson != null) {
                    Relationship relationship =
                            createNewRelationship(person, relationshipDirection, relationshipType, otherPerson);
                    saveRelationship(person, otherPerson, relationship);
                }
            }
        }
        return personService.getRelationshipsByPerson(person);
    }

    @Override
    public RelationshipDTO buildRelationshipDTO(Person person, Relationship relationship) {
        RelationshipDTO result = null;
        if (person.getId() != null) {
            if (relationship.getPersonA().getId().equals(person.getId())) {
                result = new RelationshipDTO()
                        .setUuid(relationship.getPersonB().getUuid())
                        .setType(relationship.getRelationshipType().getUuid() + PERSON_B_SUFFIX);
            } else {
                result = new RelationshipDTO()
                        .setUuid(relationship.getPersonA().getUuid())
                        .setType(relationship.getRelationshipType().getUuid() + PERSON_A_SUFFIX);
            }
        }
        return result;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Creates a new relationship based based on provided direction.
     *
     * @param person                - related person
     * @param relationshipDirection - direction of relationship
     * @param relationshipType      - type of the relationship
     * @param otherPerson           - other related person
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
     * Gets the relationships direction based on {@link RelationshipDTO} type.
     * Last character reveals relationship direction (aIsToB or bIsToA)
     *
     * @param type - the value of {@link RelationshipDTO} type
     * @return - relationship direction
     */
    private char getRelationshipDirection(String type) {
        return type.charAt(type.length() - 1);
    }

    /**
     * Gets the relationships type UUID based on {@link RelationshipDTO} type.
     * Remove flag characters at the end (used for relationship direction)
     *
     * @param type - the value of {@link RelationshipDTO} type
     * @return - relationship type UUID
     */
    private String getRelationshipTypeUuid(String type) {
        return type.substring(0, type.length() - 2);
    }

    /**
     * Verify if valid and saves new relationship in database.
     *
     * @param person       - related person
     * @param otherPerson  - related other person
     * @param relationship - relationship
     */
    private void saveRelationship(Person person, Person otherPerson, Relationship relationship) {
        if (person.getId() != null && person.getId().equals(otherPerson.getId())) {
            throw new APIException("Person A and Person B can't be the same");
        }
        personService.saveRelationship(relationship);
    }

    /**
     * Voids relationships which have been removed or changed and returns only those relationship which should be saved.
     *
     * @param receivedRelationships - current relationship
     * @param person                - related person
     * @return - relationships which should be saved
     */
    private List<RelationshipDTO> voidOldAndExtractNewRelationship(List<RelationshipDTO> receivedRelationships,
                                                                   Person person) {
        List<RelationshipDTO> alreadyExistingRelationships = new ArrayList<RelationshipDTO>();
        for (Relationship relationship : personService.getRelationshipsByPerson(person)) {
            RelationshipDTO existing = buildRelationshipDTO(person, relationship);
            if (receivedRelationships.contains(existing)) {
                alreadyExistingRelationships.add(existing);
            } else {
                personService.voidRelationship(relationship, VOIDED_REASON);
            }
        }
        return (List<RelationshipDTO>) CollectionUtils.subtract(receivedRelationships, alreadyExistingRelationships);
    }
}
