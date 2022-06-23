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

import org.junit.Test;
import org.openmrs.test.BaseContextMockTest;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CFLAddressHierarchyServiceImplTest extends BaseContextMockTest {

  @Test
  public void AddressDataValidator_shouldFailLineWithEmptyCellsInBetween() {
    final String testLine = "Belgium, Beerse,, 3590";
    final CFLAddressHierarchyServiceImpl.AddressDataValidator validator =
        new CFLAddressHierarchyServiceImpl.AddressDataValidator(testLine, ",", new ArrayList<>());

    final CFLAddressHierarchyServiceImpl.ValidatorResult validatorResult = validator.validate();

    assertFalse(validatorResult.getErrorMessage().isEmpty());
    assertTrue(validatorResult.getErrorMessage().contains("The line has empty cells."));
  }

  @Test
  public void AddressDataValidator_shouldNOTFailLineWithEmptyCellsAtTheEnd() {
    final String testLine = "Belgium, Beerse, 3590,,";
    final CFLAddressHierarchyServiceImpl.AddressDataValidator validator =
        new CFLAddressHierarchyServiceImpl.AddressDataValidator(testLine, ",", new ArrayList<>());

    final CFLAddressHierarchyServiceImpl.ValidatorResult validatorResult = validator.validate();

    assertEquals("", validatorResult.getErrorMessage());
  }
}
