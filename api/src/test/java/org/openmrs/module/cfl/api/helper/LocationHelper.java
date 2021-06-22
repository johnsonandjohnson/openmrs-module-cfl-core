package org.openmrs.module.cfl.api.helper;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.module.cfl.Constant;

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
