/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Relationship DTOs builder which used String[] provided by registration fragment to build list of DTOs.
 */
public class RelationshipDTOsBuilder {

    private String[] relationshipsTypes;
    private String[] otherPeopleUUIDs;

    public RelationshipDTOsBuilder() {
        this.relationshipsTypes = new String[0];
        this.otherPeopleUUIDs = new String[0];
    }

    public List<RelationshipDTO> build() {
        List<RelationshipDTO> relationshipDTOs = new ArrayList<RelationshipDTO>();
        for (int i = 0; i < relationshipsTypes.length; i++) {
            relationshipDTOs.add(new RelationshipDTO().setType(relationshipsTypes[i])
                    .setUuid((i < otherPeopleUUIDs.length) ? otherPeopleUUIDs[i] : null));
        }
        return relationshipDTOs;
    }

    public RelationshipDTOsBuilder withRelationshipsTypes(String[] relationshipsTypes) {
        if (relationshipsTypes == null) {
            this.relationshipsTypes = new String[0];
        } else {
            this.relationshipsTypes = Arrays.copyOf(relationshipsTypes, relationshipsTypes.length);
        }
        return this;
    }

    public RelationshipDTOsBuilder withOtherPeopleUUIDs(String[] otherPeopleUUIDs) {
        if (otherPeopleUUIDs == null) {
            this.otherPeopleUUIDs = new String[0];
        } else {
            this.otherPeopleUUIDs = Arrays.copyOf(otherPeopleUUIDs, otherPeopleUUIDs.length);
        }
        return this;
    }
}
