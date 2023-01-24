/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.util;

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

    public static Location getCurrentlyLoggedInUserLocation() {
        return Context.getUserContext().getLocation();
    }

    private LocationUtil() {
    }
}
