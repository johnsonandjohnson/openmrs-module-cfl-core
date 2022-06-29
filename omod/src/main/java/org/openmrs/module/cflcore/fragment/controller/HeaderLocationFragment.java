/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.fragment.controller;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.util.GlobalPropertyUtils;

public abstract class HeaderLocationFragment {

  protected String getLocationName(Person person) {
    String locationName = null;
    if (person != null) {
      String locationAttributeUuid =
          GlobalPropertyUtils.getGlobalProperty(
              CFLConstants.LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME);
      PersonAttribute personAttribute =
          person.getAttribute(
              Context.getPersonService().getPersonAttributeTypeByUuid(locationAttributeUuid));
      if (personAttribute != null) {
        locationName =
            Context.getLocationService().getLocationByUuid(personAttribute.getValue()).getName();
      }
    }
    return locationName;
  }
}
