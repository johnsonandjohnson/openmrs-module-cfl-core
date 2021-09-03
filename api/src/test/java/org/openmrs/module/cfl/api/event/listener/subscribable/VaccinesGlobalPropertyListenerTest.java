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
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.model.GlobalPropertyHistory;
import org.openmrs.module.cfl.api.service.GlobalPropertyHistoryService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.cfl.builder.GlobalPropertyBuilder;
import org.openmrs.module.cfl.builder.GlobalPropertyHistoryBuilder;
import org.openmrs.module.messages.api.scheduler.job.JobDefinition;
import org.openmrs.module.messages.api.scheduler.job.JobRepeatInterval;
import org.openmrs.module.messages.api.service.MessagesSchedulerService;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Context.class} )
public class VaccinesGlobalPropertyListenerTest {

    @InjectMocks
    private VaccinesGlobalPropertyListener vaccinesGlobalPropertyListener;

    @Mock
    private AdministrationService administrationService;

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private MessagesSchedulerService messagesSchedulerService;

    @Mock
    private GlobalPropertyHistoryService globalPropertyHistoryService;

    @Mock
    private MapMessage message;

    private GlobalProperty globalProperty;

    private Optional<GlobalPropertyHistory> globalPropertyHistory;

    @Before
    public void setUp() throws JMSException {
        mockStatic(Context.class);

        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getSchedulerService()).thenReturn(schedulerService);
        when(Context.getRegisteredComponent(CFLConstants.MESSAGES_SCHEDULER_SERVICE_BEAN_NAME,
                MessagesSchedulerService.class)).thenReturn(messagesSchedulerService);
        when(Context.getRegisteredComponent(CFLConstants.GLOBAL_PROPERTY_HISTORY_SERVICE_BEAN_NAME,
                GlobalPropertyHistoryService.class)).thenReturn(globalPropertyHistoryService);
        globalProperty = createTestGlobalProperty();
        globalPropertyHistory = createGlobalPropertyHistory();
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.TEST_GP_UUID);
        when(administrationService.getGlobalPropertyByUuid(Constant.TEST_GP_UUID)).thenReturn(globalProperty);
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
    public void performAction_whenGlobalPropertyIsNotNullAndUpdateRegimenJobDoesNotExistYet() {
        when(schedulerService.getTaskByName(anyString())).thenReturn(null);
        doReturn(globalPropertyHistory).when(globalPropertyHistoryService)
                .getPreviousValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);

        vaccinesGlobalPropertyListener.performAction(message);

        verify(administrationService).getGlobalPropertyByUuid(Constant.TEST_GP_UUID);
        verify(schedulerService).getTaskByName(anyString());
        verify(messagesSchedulerService)
                .createNewTask(any(JobDefinition.class), any(Instant.class), any(JobRepeatInterval.class));
    }

    @Test
    public void performAction_whenGlobalPropertyIsNotNullAndUpdateRegimenJobAlreadyExists() {
        when(schedulerService.getTaskByName(anyString())).thenReturn(new TaskDefinition());

        doReturn(globalPropertyHistory).when(globalPropertyHistoryService)
                .getPreviousValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);

        vaccinesGlobalPropertyListener.performAction(message);

        verify(administrationService).getGlobalPropertyByUuid(Constant.TEST_GP_UUID);
        verify(schedulerService).getTaskByName(anyString());
        verify(schedulerService).saveTaskDefinition(any(TaskDefinition.class));
        verifyZeroInteractions(messagesSchedulerService);
    }

    private GlobalProperty createTestGlobalProperty() {
        return new GlobalPropertyBuilder()
                .withProperty(CFLConstants.VACCINATION_PROGRAM_KEY)
                .withValue(Constant.TEST_GP_VALUE)
                .withDescription(Constant.TEST_GP_DESCRIPTION)
                .withUuid(Constant.TEST_GP_UUID)
                .build();
    }

    private Optional<GlobalPropertyHistory> createGlobalPropertyHistory() {
        return Optional.of(
                new GlobalPropertyHistoryBuilder()
                    .withId(1)
                    .withAction(Constant.UPDATE_ACTION_NAME)
                    .withActionDate(DateUtil.now())
                    .withProperty(CFLConstants.VACCINATION_PROGRAM_KEY)
                    .withValue(Constant.TEST_GP_VALUE)
                    .withDescription(Constant.TEST_GP_DESCRIPTION)
                    .build());
    }
}
