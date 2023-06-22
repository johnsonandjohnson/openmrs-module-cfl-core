package org.openmrs.module.cflcore.api.scheduler.job;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.BaseContextMockTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class ScheduledTasksCleanupJobDefinitionTest extends BaseContextMockTest {

  private static int testJobIdGenerator = 0;
  private static final String JOB_CLASS_TO_DELETE = "com.job.to.delete.class";
  private static final String JOB_OTHER_CLASS = "com.job.other.class";

  @Mock private AdministrationService administrationService;
  @Mock private SchedulerService schedulerService;

  @Before
  public void setUp() {
    contextMockHelper.setAdministrationService(administrationService);
    contextMockHelper.setService(SchedulerService.class, schedulerService);
  }

  @Test
  public void testExecute() throws SchedulerException {
    final Date now = DateUtil.now();
    final TaskDefinition taskToDelete = prepareTaskToDelete(now);
    final TaskDefinition taskWithClassToDeleteButNotDate =
        prepareTaskWithClassToDeleteButNotDate(now);
    final TaskDefinition taskWithDateToDeleteButNotClass =
        prepareTaskWithDateToDeleteButNotClass(now);
    final TaskDefinition otherTask = prepareOtherTask(now);

    when(administrationService.getGlobalProperty(CFLConstants.SCHEDULED_TASKS_TO_DELETE_CLASS_NAMES_KEY))
        .thenReturn(JOB_CLASS_TO_DELETE);
    when(schedulerService.getRegisteredTasks())
        .thenReturn(
            Arrays.asList(
                taskToDelete,
                taskWithClassToDeleteButNotDate,
                taskWithDateToDeleteButNotClass,
                otherTask));

    new ScheduledTasksCleanupJobDefinition().execute();

    verify(administrationService)
        .getGlobalProperty(CFLConstants.SCHEDULED_TASKS_TO_DELETE_CLASS_NAMES_KEY);
    verify(schedulerService).getRegisteredTasks();
    verify(schedulerService, times(1)).shutdownTask(taskToDelete);
    verify(schedulerService, never()).shutdownTask(taskWithClassToDeleteButNotDate);
    verify(schedulerService, never()).shutdownTask(taskWithDateToDeleteButNotClass);
    verify(schedulerService, never()).shutdownTask(otherTask);
    verify(schedulerService, times(1)).deleteTask(taskToDelete.getId());
    verify(schedulerService, never()).deleteTask(taskWithClassToDeleteButNotDate.getId());
    verify(schedulerService, never()).deleteTask(taskWithDateToDeleteButNotClass.getId());
    verify(schedulerService, never()).deleteTask(otherTask.getId());
  }

  private TaskDefinition prepareTaskToDelete(Date now) {
    return createTaskDefinition(JOB_CLASS_TO_DELETE, DateUtil.addDaysToDate(now, -10));
  }

  private TaskDefinition prepareTaskWithClassToDeleteButNotDate(Date now) {
    return createTaskDefinition(JOB_CLASS_TO_DELETE, DateUtil.addDaysToDate(now, -1));
  }

  private TaskDefinition prepareTaskWithDateToDeleteButNotClass(Date now) {
    return createTaskDefinition(JOB_OTHER_CLASS, DateUtil.addDaysToDate(now, -10));
  }

  private TaskDefinition prepareOtherTask(Date now) {
    return createTaskDefinition(JOB_OTHER_CLASS, DateUtil.addDaysToDate(now, -1));
  }

  private TaskDefinition createTaskDefinition(String jobClass, Date startTime) {
    final TaskDefinition taskDefinition = new TaskDefinition();
    taskDefinition.setId(++testJobIdGenerator);
    taskDefinition.setTaskClass(jobClass);
    taskDefinition.setStartTime(startTime);
    return taskDefinition;
  }
}
