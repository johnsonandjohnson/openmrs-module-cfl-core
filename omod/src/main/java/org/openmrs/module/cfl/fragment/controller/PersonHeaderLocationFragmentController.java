package org.openmrs.module.cfl.fragment.controller;

import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.util.GlobalPropertyUtils;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.api.context.Context;
import org.springframework.web.bind.annotation.RequestParam;

public class PersonHeaderLocationFragmentController {

    public void controller(FragmentModel model,
                           @RequestParam(value = "patientId", required = false) Person person) {
        model.addAttribute("personLocation", null);
        if (person != null && Context.getAuthenticatedUser().isSuperUser()) {
            String locationAttributeUuid = GlobalPropertyUtils
                    .getGlobalProperty(CFLConstants.LOCATION_ATTRIBUTE_GLOBAL_PROPERTY_NAME);
            PersonAttributeType personAttributeType = Context.getPersonService()
                    .getPersonAttributeTypeByUuid(locationAttributeUuid);
            PersonAttribute personAttribute = person.getAttribute(personAttributeType);
            if (personAttribute != null) {
                Location personLocation = Context.getLocationService()
                        .getLocationByUuid(personAttribute.getValue());
                model.addAttribute("personLocation", personLocation.getName());
            }
        }
    }
}
