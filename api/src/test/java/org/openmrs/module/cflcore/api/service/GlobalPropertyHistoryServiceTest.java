/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service;

import org.junit.Test;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.model.GlobalPropertyHistory;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class GlobalPropertyHistoryServiceTest extends BaseModuleContextSensitiveTest {

    @Autowired
    @Qualifier("cfl.globalPropertyHistoryService")
    private GlobalPropertyHistoryService globalPropertyHistoryService;

    @Test
    public void shouldFindLastVaccineValueFromGlobalPropertyHistoryTable() throws Exception {
        executeDataSet("datasets/GlobalPropertyHistoryServiceTest.xml");
        Optional<GlobalPropertyHistory> globalPropertyHistory =
                globalPropertyHistoryService.getPreviousValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        assertNotNull(globalPropertyHistory);
        assertEquals((Integer) 3, globalPropertyHistory.get().getId());
        assertEquals("Value3", globalPropertyHistory.get().getPropertyValue());
    }

    @Test
    public void shouldReturnNullValueWhenVaccineGlobalPropertyHistoryDoesNotExist() {
        Optional<GlobalPropertyHistory> globalPropertyHistory =
                globalPropertyHistoryService.getPreviousValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        assertFalse(globalPropertyHistory.isPresent());
    }
}
