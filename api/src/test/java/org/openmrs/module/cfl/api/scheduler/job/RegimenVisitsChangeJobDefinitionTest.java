package org.openmrs.module.cfl.api.scheduler.job;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.model.GlobalPropertyHistory;
import org.openmrs.module.cfl.api.service.GlobalPropertyHistoryService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Context.class} )
public class RegimenVisitsChangeJobDefinitionTest {

    private final Date date = DateUtil.now();

    @InjectMocks
    private RegimenVisitsChangeJobDefinition job ;

    @Mock
    private GlobalPropertyHistoryService globalPropertyHistoryService;

    private Optional<GlobalPropertyHistory> globalPropertyHistory;

    @Mock
    private VaccinationService vaccinationService;

    @Mock
    private AdministrationService administrationService;

    @Before
    public void setUp() {
        mockStatic(Context.class);

        globalPropertyHistory = createGlobalPropertyHistory();
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getRegisteredComponent(CFLConstants.GLOBAL_PROPERTY_HISTORY_SERVICE_BEAN_NAME,
                GlobalPropertyHistoryService.class)).thenReturn(globalPropertyHistoryService);
        when(Context.getRegisteredComponent(CFLConstants.VACCINATION_SERVICE_BEAN_NAME, VaccinationService.class))
                .thenReturn(vaccinationService);
    }

    @Test
    public void execute_whenGPValuesArePresent() {
        when(globalPropertyHistoryService.getPreviousValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY))
                .thenReturn(globalPropertyHistory);
        when(administrationService.getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY))
                .thenReturn(Constant.TEST_GP_VALUE_2);
        job.execute();

        verify(globalPropertyHistoryService)
                .getPreviousValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        verify(administrationService)
                .getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        verify(vaccinationService)
                .rescheduleVisitsBasedOnRegimenChanges(anyString(), anyString());
    }

    @Test
    public void execute_whenPreviousGPOrCurrentGpValueIsNotPresent() {
        when(globalPropertyHistoryService.getPreviousValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY))
                .thenReturn(globalPropertyHistory);
        when(administrationService.getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY)).thenReturn(null);
        job.execute();
        verify(globalPropertyHistoryService)
                .getPreviousValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        verify(administrationService)
                .getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        verifyZeroInteractions(vaccinationService);

    }

    private Optional<GlobalPropertyHistory> createGlobalPropertyHistory() {
        GlobalPropertyHistory globalPropertyHistory = new GlobalPropertyHistory();
        globalPropertyHistory.setId(1);
        globalPropertyHistory.setAction(Constant.UPDATE_ACTION_NAME);
        globalPropertyHistory.setActionDate(date);
        globalPropertyHistory.setProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        globalPropertyHistory.setPropertyValue(Constant.TEST_GP_VALUE);
        globalPropertyHistory.setDescription(Constant.TEST_GP_DESCRIPTION);
        return Optional.of(globalPropertyHistory);
    }
}
