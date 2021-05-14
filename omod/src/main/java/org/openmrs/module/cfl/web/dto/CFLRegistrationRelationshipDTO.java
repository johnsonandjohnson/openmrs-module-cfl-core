package org.openmrs.module.cfl.web.dto;

import org.apache.commons.lang.StringUtils;

/**
 * The CFLRegistrationRelationshipDTO Class.
 * <p>
 * This is representation of a definition of a single Relationship provided by CfL Registration UI.
 * </p>
 */
public class CFLRegistrationRelationshipDTO {
    private String relationshipType;
    private String otherPersonUuid;

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getOtherPersonUuid() {
        return otherPersonUuid;
    }

    public void setOtherPersonUuid(String otherPersonUuid) {
        this.otherPersonUuid = otherPersonUuid;
    }

    public String getRelationshipTypeUuid() {
        if (StringUtils.isNotEmpty(relationshipType)) {
            return relationshipType.substring(0, relationshipType.length() - 2);
        }
        return null;
    }

    public DIRECTION getRelationshipDirection() {
        if (StringUtils.isEmpty(relationshipType)) {
            return DIRECTION.UNKNOWN;
        }

        if (relationshipType.endsWith("A")) {
            return DIRECTION.A;
        } else if (relationshipType.endsWith("B")) {
            return DIRECTION.B;
        } else {
            return DIRECTION.UNKNOWN;
        }
    }

    /**
     * The Relationship direction, directly reflects which Person of Relationship is the Person defined by {@code
     * otherPersonUuid}.
     */
    public enum DIRECTION {
        /**
         * The {@code otherPersonUuid} is the A-person of the Relationship.
         */
        A,
        /**
         * The {@code otherPersonUuid} is the B-person of the Relationship.
         */
        B,
        /**
         * The person could not be determined. This indicates invalid data.
         */
        UNKNOWN;
    }
}
