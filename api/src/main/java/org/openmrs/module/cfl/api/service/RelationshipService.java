package org.openmrs.module.cfl.api.service;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.cfl.api.dto.RelationshipDTO;

import java.util.List;

public interface RelationshipService extends OpenmrsService {

    /**
     * Voids the legacy relationships for provided person and create a new one based on types from
     * {@link RelationshipDTO}.
     *
     * @param receivedRelationships - list of {@link RelationshipDTO}
     * @param person             - related person
     * @return - list of person relationships
     */
    List<Relationship> updatedRelationships(List<RelationshipDTO> receivedRelationships, Person person);

    /**
     * Creates and saves a new relationships based on types from {@link RelationshipDTO}.
     *
     * @param receivedRelationships - list of {@link RelationshipDTO}
     * @param person             - related person which already exists in the database
     * @return - list of person relationships
     */
    List<Relationship> createNewRelationships(List<RelationshipDTO> receivedRelationships, Person person);
}
