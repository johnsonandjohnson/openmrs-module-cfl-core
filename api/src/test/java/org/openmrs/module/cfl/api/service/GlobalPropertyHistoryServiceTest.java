package org.openmrs.module.cfl.api.service;

import org.junit.Test;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.model.GlobalPropertyHistory;
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
