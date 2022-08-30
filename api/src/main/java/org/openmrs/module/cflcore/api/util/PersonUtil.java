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

import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;

import java.util.Optional;

public final class PersonUtil {

  private static final String PHONE_NUMBER_EMPTY_CHAR = "-";

  public static String getPhoneNumber(Person person) {
    PersonAttribute phoneAttribute = person.getAttribute(CFLConstants.TELEPHONE_ATTRIBUTE_NAME);
    if (phoneAttribute == null || phoneAttribute.getValue().equals(PHONE_NUMBER_EMPTY_CHAR)) {
      return "";
    } else {
      return phoneAttribute.getValue();
    }
  }

  public static Optional<Location> getLocationFromAttribute(Person person) {
    final String locationAttributeName =
        Context.getAdministrationService()
            .getGlobalProperty(CFLConstants.PERSON_LOCATION_ATTRIBUTE_KEY);

    if (locationAttributeName == null) {
      return Optional.empty();
    }

    final LocationService locationService = Context.getLocationService();
    return Optional.ofNullable(person.getAttribute(locationAttributeName))
        .map(PersonAttribute::getValue)
        .map(locationService::getLocationByUuid);
  }

  private PersonUtil() {}
}
