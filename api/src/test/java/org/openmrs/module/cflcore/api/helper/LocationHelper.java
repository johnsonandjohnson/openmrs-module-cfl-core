/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.helper;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.module.cflcore.Constant;

public final class LocationHelper {

    public static Location createLocation() {
        Location location = new Location();
        location.setUuid(Constant.LOCATION_UUID);
        location.setName(Constant.LOCATION_NAME);
        location.addAttribute(createLocationAttribute("Country decoded", "BELGIUM"));
        return location;
    }

    public static LocationAttributeType createLocationAttributeType(String attributeType) {
        LocationAttributeType locationAttributeType = new LocationAttributeType();
        locationAttributeType.setName(attributeType);
        return locationAttributeType;
    }

    public static LocationAttribute createLocationAttribute(String attributeType, String value) {
        LocationAttribute locationAttribute = new LocationAttribute();
        locationAttribute.setAttributeType(createLocationAttributeType(attributeType));
        locationAttribute.setValueReferenceInternal(value);
        return locationAttribute;
    }

    public LocationHelper() {
    }
}
