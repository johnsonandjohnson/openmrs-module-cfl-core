/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.module.cflcore.api.builder.PersonDTOBuilder;
import org.openmrs.Person;
import org.openmrs.Relationship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FamilyWidgetDTO implements Serializable {

    private static final long serialVersionUID = 942937803075695682L;

    public List<PersonDTO> buildPeopleDTOs(List<Relationship> relationships, Person person) {
        List<PersonDTO> peopleDTOs = new ArrayList<PersonDTO>();
        for (Relationship relationship : relationships) {
            if (!relationship.getPersonA().getVoided() && !relationship.getPersonB().getVoided()) {
                Person relatedPerson;
                String relationshipName;
                if (relationship.getPersonA().getId().equals(person.getId())) {
                    relatedPerson = relationship.getPersonB();
                    relationshipName = relationship.getRelationshipType().getbIsToA();
                } else {
                    relatedPerson = relationship.getPersonA();
                    relationshipName = relationship.getRelationshipType().getaIsToB();
                }
                peopleDTOs.add(new PersonDTOBuilder()
                    .withPerson(relatedPerson)
                    .withRelationshipName(relationshipName)
                    .build());
            }
        }
        return peopleDTOs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
