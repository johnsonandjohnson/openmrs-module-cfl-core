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

import org.openmrs.LocationAttributeType;

public class LocationAttributeTypeBuilder extends AbstractBuilder<LocationAttributeType> {

    private Integer id;

    private String name;

    public LocationAttributeTypeBuilder() {
        super();
        id = getInstanceNumber();
        name = "Country decoded";
    }

    @Override
    public LocationAttributeType build() {
        LocationAttributeType locationAttributeType = new LocationAttributeType();
        locationAttributeType.setId(id);
        locationAttributeType.setName(name);
        return locationAttributeType;
    }

    @Override
    public LocationAttributeType buildAsNew() {
        return withId(id).withName(name).build();
    }

    public LocationAttributeTypeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public LocationAttributeTypeBuilder withName(String name) {
        this.name = name;
        return this;
    }
}
