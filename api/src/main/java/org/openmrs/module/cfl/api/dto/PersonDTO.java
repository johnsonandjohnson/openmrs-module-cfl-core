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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PersonDTO implements Serializable {

    private static final long serialVersionUID = 7631834537816381640L;

    private String uuid;
    private boolean isPatient;
    private String givenName;
    private String middleName;
    private String familyName;
    private String identifier;
    private String relationshipName;
    private String location;
    private Integer age;
    private String gender;

    public String getUuid() {
        return uuid;
    }

    public PersonDTO withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public boolean isPatient() {
        return isPatient;
    }

    public PersonDTO withIsPatient(boolean patient) {
        isPatient = patient;
        return this;
    }

    public String getGivenName() {
        return givenName;
    }

    public PersonDTO withGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public PersonDTO withMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getFamilyName() {
        return familyName;
    }

    public PersonDTO withFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public PersonDTO withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public PersonDTO withRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public PersonDTO withLocation(String location) {
        this.location = location;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public PersonDTO withAge(Integer age) {
        this.age = age;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public PersonDTO withGender(String gender) {
        this.gender = gender;
        return this;
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
