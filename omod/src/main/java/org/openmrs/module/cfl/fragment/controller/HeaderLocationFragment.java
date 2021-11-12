package org.openmrs.module.cfl.fragment.controller;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.util.GlobalPropertyUtils;

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
