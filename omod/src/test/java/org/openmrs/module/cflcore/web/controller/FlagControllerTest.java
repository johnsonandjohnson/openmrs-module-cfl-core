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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.module.cflcore.api.service.FlagDTOService;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.test.BaseContextMockTest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FlagControllerTest extends BaseContextMockTest {
  @Mock private FlagDTOService flagDTOService;

  @Before
  public void setup() {
    contextMockHelper.setService(FlagDTOService.class, flagDTOService);
  }

  @Test
  public void shouldReturnAllNotRetiredAndEnabledFlags() {
    final Flag flag = new Flag();
    final Flag disabledFlag = new Flag();
    disabledFlag.setEnabled(Boolean.FALSE);
    final Flag retiredFlag = new Flag();
    retiredFlag.setRetired(Boolean.TRUE);

    final FlagController controller = new FlagController();
    controller.getAllFlags();

    verify(flagDTOService, times(1)).getAllEnabledFlags();
  }
}
