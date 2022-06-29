/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.event.listener.subscribable;

import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.scheduler.job.RegimenVisitsChangeJobDefinition;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.module.messages.api.scheduler.job.JobRepeatInterval;
import org.openmrs.module.messages.api.service.MessagesSchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The VaccinesGlobalPropertyListener class.
 *
 * This listener is responsible for creating scheduled task if global property
 * {@link CFLConstants#VACCINATION_PROGRAM_KEY} is updated and the task does not exist yet.
 *
 * The task is scheduled for midnight server time and executes logic for rescheduling patient visits based on changes
 * in mentioned vaccines global property.
 *
 */
public class VaccinesGlobalPropertyListener extends GlobalPropertyActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaccinesGlobalPropertyListener.class);

    @Override
    public List<String> subscribeToActions() {
        return Collections.singletonList(Event.Action.UPDATED.name());
    }

    @Override
    public void performAction(Message message) {
        LOGGER.debug("Vaccines global property action listener triggered");
        GlobalProperty updatedGP = extractGlobalProperty(message);
        if (StringUtils.equals(updatedGP.getProperty(), CFLConstants.VACCINATION_PROGRAM_KEY)) {
            configureOrCreateTask();
        }
    }

    private void configureOrCreateTask() {
        TaskDefinition updateRegimenTask = getTaskByName(CFLConstants.UPDATE_REGIMEN_JOB_NAME);
        if (updateRegimenTask != null) {
            setTimeAndEnableTask(updateRegimenTask);
        } else {
            getMessagesSchedulerService().createNewTask(new RegimenVisitsChangeJobDefinition(),
                    getTaskStartTime().toInstant(), JobRepeatInterval.NEVER);
        }
        LOGGER.info("Task with name: " + CFLConstants.UPDATE_REGIMEN_JOB_NAME +
                " has been configured and will be executed at: " + getTaskStartTime());
    }

    private TaskDefinition getTaskByName(String taskName) {
        return Context.getSchedulerService().getTaskByName(taskName);
    }

    private void setTimeAndEnableTask(TaskDefinition task) {
        task.setStartTime(getTaskStartTime());
        task.setStarted(true);
        Context.getSchedulerService().saveTaskDefinition(task);
    }

    private Date getTaskStartTime() {
        return DateUtil.getDateWithTimeOfDay(DateUtil.addDaysToDate(
                DateUtil.nowInSystemTimeZone(), 1), DateUtil.MIDNIGHT_TIME, DateUtil.getSystemTimeZone());
    }

    private MessagesSchedulerService getMessagesSchedulerService() {
        return Context.getRegisteredComponent(CFLConstants.MESSAGES_SCHEDULER_SERVICE_BEAN_NAME,
                MessagesSchedulerService.class);
    }
}
