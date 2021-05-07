package org.openmrs.module.cfl.api.helper;

import org.openmrs.Location;
import org.openmrs.module.cfl.Constant;

public final class LocationHelper {

    public static Location createLocation() {
        Location location = new Location();
        location.setUuid(Constant.LOCATION_UUID);
        location.setName(Constant.LOCATION_NAME);
        return location;
    }

    public LocationHelper() {
    }
}
