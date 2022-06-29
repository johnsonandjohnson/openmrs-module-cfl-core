/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.web.dto;

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
        if (StringUtils.isNotBlank(relationshipType)) {
            return relationshipType.substring(0, relationshipType.length() - 2);
        }
        return null;
    }

    public DIRECTION getRelationshipDirection() {
        final DIRECTION result;

        if (StringUtils.isBlank(relationshipType)) {
            result = DIRECTION.UNKNOWN;
        } else if (relationshipType.endsWith("A")) {
            result = DIRECTION.A;
        } else if (relationshipType.endsWith("B")) {
            result = DIRECTION.B;
        } else {
            result = DIRECTION.UNKNOWN;
        }

        return result;
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
