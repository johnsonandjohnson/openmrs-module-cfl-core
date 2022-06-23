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

import org.openmrs.Location;
import org.openmrs.LocationAttribute;

public class LocationBuilder extends AbstractBuilder<Location> {

    private Integer id;

    private String name;

    private LocationAttribute locationAttribute;

    public LocationBuilder() {
        super();
        id = getInstanceNumber();
        name = "Test name";
        locationAttribute = new LocationAttributeBuilder().build();
    }

    @Override
    public Location build() {
        Location location = new Location();
        location.setId(id);
        location.setName(name);
        location.setAttribute(locationAttribute);
        return location;
    }

    @Override
    public Location buildAsNew() {
        return withId(id).withName(name).withAttribute(locationAttribute).build();
    }

    public LocationBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public LocationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public LocationBuilder withAttribute(LocationAttribute locationAttribute) {
        this.locationAttribute = locationAttribute;
        return this;
    }
}
