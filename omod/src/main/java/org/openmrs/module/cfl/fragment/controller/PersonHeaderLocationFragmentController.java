package org.openmrs.module.cfl.fragment.controller;

import org.openmrs.Person;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

public class PersonHeaderLocationFragmentController extends HeaderLocationFragment {

  private static final String PERSON_LOCATION_ATTRIBUTE_NAME = "personLocation";

  public void controller(
      FragmentModel model, @RequestParam(value = "patientId", required = false) Person person) {
    model.addAttribute(PERSON_LOCATION_ATTRIBUTE_NAME, getLocationName(person));
  }
}
