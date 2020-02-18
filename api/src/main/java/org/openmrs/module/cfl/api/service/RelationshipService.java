package org.openmrs.module.cfl.api.service;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.OpenmrsService;

import java.util.List;

public interface RelationshipService extends OpenmrsService {

    /**
     * Voids the legacy relationships for provided person and create a new one based on types from
     * {@link org.openmrs.module.cfl.api.domain.RelationshipDTO}.
     *
     * @param relationshipsTypes - list of {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} types
     * @param otherPeopleUUIDs   - list of {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} other people UUIDs
     * @param person             - related person
     * @return - list of new relationships creates based on {@link org.openmrs.module.cfl.api.domain.RelationshipDTO}
     */
    List<Relationship> updatedRelationships(String[] relationshipsTypes, String[] otherPeopleUUIDs, Person person);

    /**
     * Creates and saves a new relationships based on types from {@link org.openmrs.module.cfl.api.domain.RelationshipDTO}.
     *
     * @param relationshipsTypes - list of {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} types
     * @param otherPeopleUUIDs   - list of {@link org.openmrs.module.cfl.api.domain.RelationshipDTO} other people UUIDs
     * @param person             - related person which already exists in the database
     * @return - list of new relationships creates based on {@link org.openmrs.module.cfl.api.domain.RelationshipDTO}
     */
    List<Relationship> createNewRelationships(String[] relationshipsTypes, String[] otherPeopleUUIDs, Person person);
}
