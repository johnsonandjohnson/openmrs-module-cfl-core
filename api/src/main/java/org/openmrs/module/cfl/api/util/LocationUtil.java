package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.api.context.Context;

public final class LocationUtil {

    public static LocationAttribute getLocationAttributeByPersonAndAttributeTypeName(Person person,
                                                                                     String locationAttrTypeName) {
        LocationAttribute locationAttribute = null;
        PatientIdentifier patientIdentifier = PatientUtil.getPatientIdentifier(person);
        LocationAttributeType locationAttrType =
                Context.getLocationService().getLocationAttributeTypeByName(locationAttrTypeName);
        Location location = patientIdentifier.getLocation();
        for (LocationAttribute locationAttr : location.getActiveAttributes()) {
            if (locationAttrType != null && StringUtils.equalsIgnoreCase(locationAttrType.getName(),
                    locationAttr.getAttributeType().getName())) {
                locationAttribute = locationAttr;
                break;
            }
        }
        return locationAttribute;
    }

    private LocationUtil() {
    }
}
