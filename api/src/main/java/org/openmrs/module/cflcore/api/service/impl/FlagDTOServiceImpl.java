/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.dto.FlagDTO;
import org.openmrs.module.cflcore.api.service.FlagDTOService;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;

import java.util.List;
import java.util.stream.Collectors;

public class FlagDTOServiceImpl implements FlagDTOService {
  @Override
  public List<FlagDTO> getAllEnabledFlags() {
    FlagService flagService = Context.getService(FlagService.class);
    return flagService.getAllFlags().stream()
        .filter(flag -> !flag.getRetired())
        .filter(Flag::getEnabled)
        .map(FlagDTO::new)
        .collect(Collectors.toList());
  }
}
