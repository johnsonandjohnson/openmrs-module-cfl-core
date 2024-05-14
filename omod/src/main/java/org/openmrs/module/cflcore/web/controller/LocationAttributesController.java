/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.web.controller;

import org.openmrs.module.visits.api.dto.LocationAttributeDTO;
import org.openmrs.module.visits.api.util.LocationsAttributesUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("cfl.locationAttributesController")
@RequestMapping(value = "/cfl/locationAttributes")
public class LocationAttributesController {

  @RequestMapping(value = "", method = RequestMethod.GET)
  @ResponseBody
  public List<LocationAttributeDTO> getLocationsAttributes() {
    return LocationsAttributesUtil.getLocationsAttributes();
  }
}
