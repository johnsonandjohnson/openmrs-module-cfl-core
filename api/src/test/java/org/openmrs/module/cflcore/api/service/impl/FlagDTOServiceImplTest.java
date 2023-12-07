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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.module.cflcore.api.dto.FlagDTO;
import org.openmrs.module.cflcore.api.service.FlagDTOService;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.test.BaseContextMockTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FlagDTOServiceImplTest extends BaseContextMockTest {
  @Mock private FlagService flagService;

  @Before
  public void setup() {
    contextMockHelper.setService(FlagService.class, flagService);
  }

  @Test
  public void shouldReturnAllNotRetiredAndEnabledFlags() {
    final Flag flag = new Flag();
    final Flag disabledFlag = new Flag();
    disabledFlag.setEnabled(Boolean.FALSE);
    final Flag retiredFlag = new Flag();
    retiredFlag.setRetired(Boolean.TRUE);

    when(flagService.getAllFlags()).thenReturn(Arrays.asList(flag, disabledFlag, retiredFlag));

    final FlagDTOService flagDTOService = new FlagDTOServiceImpl();
    final List<FlagDTO> dtos = flagDTOService.getAllEnabledFlags();

    assertEquals(1, dtos.size());
    assertEquals(flag.getUuid(), dtos.get(0).getUuid());
  }
}
