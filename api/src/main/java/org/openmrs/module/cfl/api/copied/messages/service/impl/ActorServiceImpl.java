/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.copied.messages.service.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.copied.messages.model.ActorType;
import org.openmrs.module.cfl.api.copied.messages.model.RelationshipTypeDirection;
import org.openmrs.module.cfl.api.copied.messages.service.ActorService;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ActorServiceImpl implements ActorService {

    private static final Log LOGGER = LogFactory.getLog(ActorServiceImpl.class);

    private static final String COMMA_SEPARATOR = ",";

    private static final String SEMICOLON_SEPARATOR = ":";

    private static final int RELATIONSHIP_TYPE_POSITION = 0;

    private static final int RELATIONSHIP_DIRECTION_POSITION = 1;

    private ConfigService configService;

    @Override
    public boolean isInCaregiverRole(Person person) {
        if (person == null) {
            throw new CflRuntimeException("Person cannot be null.");
        }

        List<ActorType> actorTypes = getAllActorTypes();
        for (ActorType actorType : actorTypes) {
            if (BooleanUtils.isTrue(isInCaregiverRole(person, actorType))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ActorType> getAllActorTypes() {
        String actorTypeConfig = configService.getActorTypesConfiguration();
        if (StringUtils.isBlank(actorTypeConfig)) {
            return Collections.emptyList();
        }
        String[] parts = separateTheActorTypes(actorTypeConfig);
        return parseTheActorTypes(parts);
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    private boolean isInCaregiverRole(Person person, ActorType actorType) {
        boolean isInAnyRelationship = false;
        if (RelationshipTypeDirection.A.equals(actorType.getDirection())) {
            isInAnyRelationship = isInCaregiverRoleFromPerson(person, actorType.getRelationshipType());
        } else if (RelationshipTypeDirection.B.equals(actorType.getDirection())) {
            isInAnyRelationship = isInCaregiverRoleToPerson(person, actorType.getRelationshipType());
        }
        return isInAnyRelationship;
    }

    private boolean isInCaregiverRoleToPerson(Person person, RelationshipType relationshipType) {
        List<Relationship> relationships = Context.getPersonService().getRelationships(null,
                person, relationshipType);
        return !relationships.isEmpty();
    }

    private boolean isInCaregiverRoleFromPerson(Person person, RelationshipType relationshipType) {
        List<Relationship> relationships = Context.getPersonService().getRelationships(person,
                null, relationshipType);
        return !relationships.isEmpty();
    }

    private String[] separateTheActorTypes(String actorTypeConfig) {
        String[] parts = { actorTypeConfig };
        if (actorTypeConfig.contains(COMMA_SEPARATOR)) {
            parts = actorTypeConfig.split(COMMA_SEPARATOR);
        }
        return parts;
    }

    private List<ActorType> parseTheActorTypes(String[] actorTypes) {
        List<ActorType> result = new LinkedList<ActorType>();
        for (String actorTypeDefinition : actorTypes) {
            ActorType actorType = parseActorType(actorTypeDefinition);
            if (actorType != null) {
                result.add(actorType);
            }
        }
        return result;
    }

    private ActorType parseActorType(String actorTypeDefinition) {
        RelationshipType relationshipType = parseRelationshipType(actorTypeDefinition);
        RelationshipTypeDirection direction = parseRelationshipTypeDirection(actorTypeDefinition);
        if (relationshipType == null) {
            LOGGER.warn(String.format("Not exist relationship described by this definition: %s", actorTypeDefinition));
            return null;
        }
        return new ActorType(relationshipType, direction);
    }

    private RelationshipType parseRelationshipType(String actorTypeDefinition) {
        RelationshipType relationshipType = null;
        String relationshipTypeUUID = actorTypeDefinition;
        if (actorTypeDefinition.contains(SEMICOLON_SEPARATOR)) {
            relationshipTypeUUID = actorTypeDefinition.split(SEMICOLON_SEPARATOR)[RELATIONSHIP_TYPE_POSITION];
        }
        relationshipType = Context.getPersonService().getRelationshipTypeByUuid(relationshipTypeUUID);
        return relationshipType;
    }

    private RelationshipTypeDirection parseRelationshipTypeDirection(String actorTypeDefinition) {
        String direction = configService.getDefaultActorRelationDirection();
        if (actorTypeDefinition.contains(SEMICOLON_SEPARATOR)) {
            String[] actorDefinition = actorTypeDefinition.split(SEMICOLON_SEPARATOR);
            if (actorDefinition.length > 1) {
                try {
                    RelationshipTypeDirection.valueOf(actorDefinition[RELATIONSHIP_DIRECTION_POSITION]);
                    direction = actorDefinition[RELATIONSHIP_DIRECTION_POSITION];
                } catch (IllegalArgumentException exception) {
                    LOGGER.warn(String.format(
                            "The %s isn't correct value for RelationshipTypeDirection enum. Default will be used.",
                            actorDefinition[RELATIONSHIP_DIRECTION_POSITION]),
                            exception);
                }
            }
        }
        return RelationshipTypeDirection.valueOf(direction);
    }
}
