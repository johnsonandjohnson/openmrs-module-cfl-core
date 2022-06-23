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

import org.openmrs.GlobalProperty;

import java.util.UUID;

public class GlobalPropertyBuilder extends AbstractBuilder<GlobalProperty> {

    private String property;

    private String value;

    private String description;

    private String uuid;

    public GlobalPropertyBuilder() {
        super();
        property = "Test GP name";
        value = "Test GP value";
        description = "Test GP description";
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public GlobalProperty build() {
        GlobalProperty globalProperty = new GlobalProperty();
        globalProperty.setProperty(property);
        globalProperty.setPropertyValue(value);
        globalProperty.setDescription(description);
        globalProperty.setUuid(uuid);
        return globalProperty;
    }

    @Override
    public GlobalProperty buildAsNew() {
        return withProperty("test name").build();
    }

    public GlobalPropertyBuilder withProperty(String property) {
        this.property = property;
        return this;
    }

    public GlobalPropertyBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public GlobalPropertyBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public GlobalPropertyBuilder withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }
}
