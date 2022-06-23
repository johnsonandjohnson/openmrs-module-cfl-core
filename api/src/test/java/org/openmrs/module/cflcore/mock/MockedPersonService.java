/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.mock;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.PersonServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

/**
 * Mock of Person Service prepared to verify functionalities provided by
 * {@link org.openmrs.module.cflcore.api.service.RelationshipService}
 */
public class MockedPersonService extends PersonServiceImpl {

    private List<Person> people;
    private List<RelationshipType> relationshipTypes;
    private List<Relationship> relationships;
    private static Integer relationshipsId = 1;

    public MockedPersonService() {
        this.people = new ArrayList<Person>();
        this.relationshipTypes = new ArrayList<RelationshipType>();
        this.relationships = new ArrayList<Relationship>();
    }

    @Override
    public RelationshipType getRelationshipTypeByUuid(String uuid) throws APIException {
        return getByUuid(relationshipTypes, uuid);
    }

    @Override
    public Relationship saveRelationship(Relationship relationship) throws APIException {
        if (relationship.getId() == null) {
            relationship.setRelationshipId(relationshipsId++);
        }
        setUuidIfNeeded(relationship);
        return addOrUpdate(relationships, relationship);
    }

    @Override
    public Person getPersonByUuid(String uuid) throws APIException {
        return getByUuid(people, uuid);
    }

    @Override
    public List<Relationship> getRelationshipsByPerson(Person p) throws APIException {
        List<Relationship> result = new ArrayList<Relationship>();
        for (Relationship relationship : relationships) {
            if (!relationship.isVoided() && (p.getUuid().equals(relationship.getPersonA().getUuid()) ||
                    p.getUuid().equals(relationship.getPersonB().getUuid()))) {
                result.add(relationship);
            }
        }
        return result;
    }

    @Override
    public Relationship voidRelationship(Relationship relationship, String voidReason) throws APIException {
        return voidObject(relationships, relationship, voidReason);
    }

    public MockedPersonService withPeople(List<Person> people) {
        this.people = new ArrayList<Person>(people);
        return this;
    }

    public MockedPersonService withRelationshipTypes(List<RelationshipType> relationshipTypes) {
        this.relationshipTypes = new ArrayList<RelationshipType>(relationshipTypes);
        return this;
    }

    public MockedPersonService withRelationships(List<Relationship> relationships) {
        this.relationships = new ArrayList<Relationship>(relationships);
        return this;
    }

    private <T extends BaseOpenmrsObject> T getByUuid(List<T> values, String uuid) {
        if (StringUtils.isNotBlank(uuid)) {
            for (T object : values) {
                if (uuid.equals(object.getUuid())) {
                    return object;
                }
            }
        }
        return null;
    }

    private void setUuidIfNeeded(BaseOpenmrsObject object) {
        if (StringUtils.isBlank(object.getUuid())) {
            object.setUuid(UUID.randomUUID().toString());
        }
    }

    private <T extends BaseOpenmrsObject> T addOrUpdate(List<T> values, T newValue) {
        ListIterator<T> iterator = values.listIterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (next.getId().equals(newValue.getId()) || next.getUuid().equals(newValue.getUuid())) {
                //Replace element
                iterator.set(newValue);
                return newValue;
            }
        }
        values.add(newValue);
        return newValue;
    }

    private  <T extends BaseOpenmrsData> T voidObject(List<T> values, T relatedObject, String voidReason) {
        T object = getByUuid(values, relatedObject.getUuid());
        if (object == null) {
            throw new APIException(String.format("Missing object with uuid %s", relatedObject.getUuid()));
        }
        object.setVoided(true);
        object.setVoidReason(voidReason);
        object.setDateVoided(new Date());
        return object;
    }
}
