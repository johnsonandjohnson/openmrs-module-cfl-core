/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.builder;

import org.openmrs.RelationshipType;
import org.openmrs.module.cflcore.Constant;

import java.util.UUID;

public class RelationshipTypeBuilder extends AbstractBuilder<RelationshipType> {

    private String aIsToB;
    private String bIsToA;
    private Integer id;
    private Boolean preferred;
    private String uuid;
    private Integer weight;

    public RelationshipTypeBuilder() {
        this.aIsToB = "Patient";
        this.bIsToA = "Caregiver";
        this.id = getInstanceNumber();
        this.preferred = false;
        this.weight = 0;
        this.uuid = Constant.RELATIONSHIP_TYPE_UUID;
    }

    @Override
    public RelationshipType build() {
        RelationshipType relationshipType = new RelationshipType();
        relationshipType.setaIsToB(this.aIsToB);
        relationshipType.setbIsToA(this.bIsToA);
        relationshipType.setId(this.id);
        relationshipType.setPreferred(this.preferred);
        relationshipType.setRelationshipTypeId(this.id);
        relationshipType.setWeight(this.weight);
        relationshipType.setUuid(this.uuid);
        return relationshipType;
    }

    @Override
    public RelationshipType buildAsNew() {
        return this.withId(null).withUuid(UUID.randomUUID().toString()).build();
    }

    public RelationshipTypeBuilder withAIsToB(String aIsToB) {
        this.aIsToB = aIsToB;
        return this;
    }

    public RelationshipTypeBuilder withBIsToA(String bIsToA) {
        this.bIsToA = bIsToA;
        return this;
    }

    public RelationshipTypeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public RelationshipTypeBuilder withPreferred(Boolean preferred) {
        this.preferred = preferred;
        return this;
    }

    public RelationshipTypeBuilder withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public RelationshipTypeBuilder withWeight(Integer weight) {
        this.weight = weight;
        return this;
    }
}
