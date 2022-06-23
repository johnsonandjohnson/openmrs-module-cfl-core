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

import org.openmrs.PersonAttributeType;

public class PersonAttributeTypeBuilder extends AbstractBuilder<PersonAttributeType> {

    private Integer id;

    private String name;

    private String description;

    private String format;

    private Boolean searchable;

    public PersonAttributeTypeBuilder() {
        super();
        id = getInstanceNumber();
        name = "Telephone Number";
        description = "The telephone number for the person";
        format = "java.lang.String";
        searchable = false;
    }

    @Override
    public PersonAttributeType build() {
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setId(id);
        personAttributeType.setName(name);
        personAttributeType.setDescription(description);
        personAttributeType.setFormat(format);
        personAttributeType.setSearchable(searchable);
        return personAttributeType;
    }

    @Override
    public PersonAttributeType buildAsNew() {
        return withId(null).build();
    }

    public PersonAttributeTypeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public PersonAttributeTypeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonAttributeTypeBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public PersonAttributeTypeBuilder withFormat(String format) {
        this.format = format;
        return this;
    }

    public PersonAttributeTypeBuilder withSearchable(Boolean searchable) {
        this.searchable = searchable;
        return this;
    }
}
