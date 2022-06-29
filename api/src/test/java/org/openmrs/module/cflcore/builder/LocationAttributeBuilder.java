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

import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;

public class LocationAttributeBuilder extends AbstractBuilder<LocationAttribute> {

    private Integer id;

    private String value;

    private LocationAttributeType locationAttributeType;

    public LocationAttributeBuilder() {
        super();
        id = getInstanceNumber();
        value = "Poland";
        locationAttributeType = new LocationAttributeTypeBuilder().build();
    }

    @Override
    public LocationAttribute build() {
        LocationAttribute locationAttribute = new LocationAttribute();
        locationAttribute.setId(id);
        locationAttribute.setValueReferenceInternal(value);
        locationAttribute.setAttributeType(locationAttributeType);
        return locationAttribute;
    }

    @Override
    public LocationAttribute buildAsNew() {
        return withId(id).withValue(value).withAttributeType(locationAttributeType).build();
    }

    public LocationAttributeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public LocationAttributeBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public LocationAttributeBuilder withAttributeType(LocationAttributeType locationAttributeType) {
        this.locationAttributeType = locationAttributeType;
        return this;
    }
}
