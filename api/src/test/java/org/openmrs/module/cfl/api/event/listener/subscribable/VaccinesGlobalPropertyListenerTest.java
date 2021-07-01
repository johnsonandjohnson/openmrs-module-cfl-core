package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.model.GlobalPropertyHistory;
import org.openmrs.module.cfl.api.service.GlobalPropertyHistoryService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.messages.api.service.MessagesSchedulerService;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Context.class} )
public class VaccinesGlobalPropertyListenerTest {

    private static final String TEST_GP_UUID = "2096bec8-99f3-480c-86e3-2df08f375b93";

    private static final String TEST_GP_VALUE = "Test value";

    private static final String TEST_GP_DESCRIPTION = "Test description";

    private static final String UPDATE_ACTION_NAME = "UPDATE";

    private final Date date = DateUtil.now();

    @Mock
    private AdministrationService administrationService;

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private MessagesSchedulerService messagesSchedulerService;

    @Mock
    private GlobalPropertyHistoryService globalPropertyHistoryService;

    @InjectMocks
    private VaccinesGlobalPropertyListener vaccinesGlobalPropertyListener;

    @Mock
    private MapMessage message;

    private GlobalProperty globalProperty;

    private GlobalPropertyHistory globalPropertyHistory;

    @Before
    public void setUp() {
        mockStatic(Context.class);

        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getSchedulerService()).thenReturn(schedulerService);
        when(Context.getRegisteredComponent(CFLConstants.MESSAGES_SCHEDULER_SERVICE_BEAN_NAME,
                MessagesSchedulerService.class)).thenReturn(messagesSchedulerService);
        when(Context.getRegisteredComponent(CFLConstants.GLOBAL_PROPERTY_HISTORY_SERVICE_BEAN_NAME,
                GlobalPropertyHistoryService.class)).thenReturn(globalPropertyHistoryService);
        globalProperty = createTestGlobalProperty();
        globalPropertyHistory = createGlobalPropertyHistory();
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionWhenGlobalPropertyRetrievingFailed() {
        try {
            vaccinesGlobalPropertyListener.performAction(null);
        } catch (CflRuntimeException ex) {
            verifyZeroInteractions(schedulerService);
            verifyZeroInteractions(messagesSchedulerService);
            verifyZeroInteractions(globalPropertyHistoryService);
        }
    }

    @Test
    public void performAction_whenGlobalPropertyIsNotNull() throws JMSException {
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(TEST_GP_UUID);
        when(administrationService.getGlobalPropertyByUuid(TEST_GP_UUID))
                .thenReturn(globalProperty);
        assertEquals(CFLConstants.VACCINATION_PROGRAM_KEY, globalProperty.getProperty());

        doReturn(globalPropertyHistory).when(globalPropertyHistoryService)
                .getLastValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);

        vaccinesGlobalPropertyListener.performAction(message);
        verify(administrationService, times(1)).getGlobalPropertyByUuid(TEST_GP_UUID);
        verify(schedulerService, times(1)).getTaskByName(anyString());
        verify(globalPropertyHistoryService, times(2)).getLastValueOfGlobalProperty(anyString());
        verify(messagesSchedulerService, times(1)).createNewTask(any(), any(), any());
    }

    @Test
    public void performAction_whenRelatedTaskAlreadyExists() throws JMSException {
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(TEST_GP_UUID);
        when(administrationService.getGlobalPropertyByUuid(TEST_GP_UUID))
                .thenReturn(globalProperty);
        assertEquals(CFLConstants.VACCINATION_PROGRAM_KEY, globalProperty.getProperty());

        doReturn(globalPropertyHistory).when(globalPropertyHistoryService)
                .getLastValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);

        when(schedulerService.getTaskByName(anyString())).thenReturn(new TaskDefinition());

        vaccinesGlobalPropertyListener.performAction(message);
        verifyZeroInteractions(messagesSchedulerService);
    }

    private GlobalProperty createTestGlobalProperty() {
        GlobalProperty globalProperty = new GlobalProperty();
        globalProperty.setProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        globalProperty.setPropertyValue(TEST_GP_VALUE);
        globalProperty.setDescription(TEST_GP_DESCRIPTION);
        globalProperty.setUuid(TEST_GP_UUID);
        return globalProperty;
    }

    private GlobalPropertyHistory createGlobalPropertyHistory() {
        GlobalPropertyHistory globalPropertyHistory = new GlobalPropertyHistory();
        globalPropertyHistory.setId(1);
        globalPropertyHistory.setAction(UPDATE_ACTION_NAME);
        globalPropertyHistory.setActionDate(date);
        globalPropertyHistory.setProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        globalPropertyHistory.setPropertyValue(TEST_GP_VALUE);
        globalPropertyHistory.setDescription(TEST_GP_DESCRIPTION);
        return globalPropertyHistory;
    }
}
