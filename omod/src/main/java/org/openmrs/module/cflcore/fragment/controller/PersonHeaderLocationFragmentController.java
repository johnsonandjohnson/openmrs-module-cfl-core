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
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

public class PersonHeaderLocationFragmentController extends HeaderLocationFragment {

  private static final String PERSON_LOCATION_ATTRIBUTE_NAME = "personLocation";

  public void controller(
      FragmentModel model, @RequestParam(value = "patientId", required = false) Person person) {
    model.addAttribute(PERSON_LOCATION_ATTRIBUTE_NAME, getLocationName(person));
  }
}
