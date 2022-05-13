/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.builder;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;

public class PersonAttributeBuilder extends AbstractBuilder<PersonAttribute> {

    private Integer id;

    private Person person;

    private String value;

    private PersonAttributeType personAttributeType;

    private boolean voided;

    public PersonAttributeBuilder() {
        super();
        id = getInstanceNumber();
        person = new PersonBuilder().build();
        value = "48100200300";
        personAttributeType = new PersonAttributeTypeBuilder().build();
        voided = false;
    }

    @Override
    public PersonAttribute build() {
        PersonAttribute personAttribute = new PersonAttribute();
        personAttribute.setId(id);
        personAttribute.setPerson(person);
        personAttribute.setValue(value);
        personAttribute.setAttributeType(personAttributeType);
        personAttribute.setVoided(voided);
        return personAttribute;
    }

    @Override
    public PersonAttribute buildAsNew() {
        return withId(null).build();
    }

    public PersonAttributeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public PersonAttributeBuilder withPerson(Person person) {
        this.person = person;
        return this;
    }

    public PersonAttributeBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public PersonAttributeBuilder withPersonAttributeType(PersonAttributeType personAttributeType) {
        this.personAttributeType = personAttributeType;
        return this;
    }

    public PersonAttributeBuilder withVoided(boolean voided) {
        this.voided = voided;
        return this;
    }
}
