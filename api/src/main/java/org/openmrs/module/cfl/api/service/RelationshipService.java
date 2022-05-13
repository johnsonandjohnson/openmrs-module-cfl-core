/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
     * @param person                - related person
     * @return - list of person relationships
     */
    List<Relationship> updatedRelationships(List<RelationshipDTO> receivedRelationships, Person person);

    /**
     * Creates and saves a new relationships based on types from {@link RelationshipDTO}.
     *
     * @param receivedRelationships - list of {@link RelationshipDTO}
     * @param person                - related person which already exists in the database
     * @return - list of person relationships
     */
    List<Relationship> createNewRelationships(List<RelationshipDTO> receivedRelationships, Person person);

    /**
     * Builds {@link RelationshipDTO} based on existing relationship and existing person (required person id).
     *
     * @param person       - related person used to determine the relationship direction
     * @param relationship - related relationship
     * @return - the DTO representation. Returns null if missing person id
     */
    RelationshipDTO buildRelationshipDTO(Person person, Relationship relationship);
}
