/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.scheduler.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.messages.api.scheduler.job.JobDefinition;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.cflcore.api.util.DateUtil.getMinusOneWeekIntervalDate;

public class ScheduledTasksCleanupJobDefinition extends JobDefinition {
  private static final Log LOGGER = LogFactory.getLog(ScheduledTasksCleanupJobDefinition.class);

  @Override
  public void execute() {
    LOGGER.info(CFLConstants.SCHEDULED_TASKS_CLEANUP_JOB_NAME + " started");

    final String classNamesGPValue =
        Context.getAdministrationService()
            .getGlobalProperty(CFLConstants.SCHEDULED_TASKS_TO_DELETE_CLASS_NAMES_KEY);
    final List<String> classToDeleteNames = Arrays.asList(classNamesGPValue.split(","));
    final SchedulerService schedulerService = Context.getSchedulerService();

    final Collection<TaskDefinition> tasksToDelete =
        schedulerService.getRegisteredTasks().stream()
            .filter(task -> classToDeleteNames.contains(task.getTaskClass()))
            .filter(task -> task.getStartTime().before(getMinusOneWeekIntervalDate()))
            .collect(Collectors.toSet());

    LOGGER.info("Found " + tasksToDelete.size() + " tasks to delete");
    tasksToDelete.forEach(task -> deleteTask(schedulerService, task));
  }

  @Override
  public boolean shouldStartAtFirstCreation() {
    // Execute on each startup
    return false;
  }

  @Override
  public String getTaskName() {
    return CFLConstants.SCHEDULED_TASKS_CLEANUP_JOB_NAME;
  }

  @Override
  public Class getTaskClass() {
    return ScheduledTasksCleanupJobDefinition.class;
  }

  private void deleteTask(SchedulerService schedulerService, TaskDefinition task) {
    try {
      schedulerService.shutdownTask(task);
      schedulerService.deleteTask(task.getId());
    } catch (SchedulerException e) {
      LOGGER.error("Failed to stop or delete task: " + task.getName(), e);
    }
  }
}
