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
