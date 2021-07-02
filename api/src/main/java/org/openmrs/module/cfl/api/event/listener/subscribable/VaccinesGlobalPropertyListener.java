package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.scheduler.job.RegimenVisitsChangeJobDefinition;
import org.openmrs.module.cfl.api.service.GlobalPropertyHistoryService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.messages.api.scheduler.job.JobRepeatInterval;
import org.openmrs.module.messages.api.service.MessagesSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        if (StringUtils.equals(updatedGP.getProperty(), CFLConstants.VACCINATION_PROGRAM_KEY) &&
                (isTaskNotExists(getFullUpdateRegimenTaskName()))) {
                getMessagesSchedulerService().createNewTask(new RegimenVisitsChangeJobDefinition(),
                        getUpdateRegimenTaskExecutionDate(), JobRepeatInterval.NEVER);
        } else {
            LOGGER.info(String.format("Task with name %s already exists", getFullUpdateRegimenTaskName()));
        }
    }

    private boolean isTaskNotExists(String taskName) {
        return Context.getSchedulerService().getTaskByName(taskName) == null;
    }

    private String getFullUpdateRegimenTaskName() {
        return CFLConstants.UPDATE_REGIMEN_TASK_NAME_PREFIX + getUpdateRegimenTaskExecutionDateToString();
    }

    private String getUpdateRegimenTaskExecutionDateToString() {
        return DateUtil.convertDate(getUpdateRegimenTaskExecutionDate(), DateUtil.DATE_AND_TIME_AND_TIME_ZONE_PATTERN);
    }

    private Date getUpdateRegimenTaskExecutionDate() {
        Date previousGPUpdateDate = getGlobalPropertyHistoryService()
                .getLastValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY).getActionDate();
        return DateUtil.getDateWithTimeOfDay(DateUtil.addDaysToDate(previousGPUpdateDate, 1),
                DateUtil.MIDNIGHT_TIME, DateUtil.getSystemTimeZone());
    }

    private GlobalPropertyHistoryService getGlobalPropertyHistoryService() {
        return Context.getRegisteredComponent(CFLConstants.GLOBAL_PROPERTY_HISTORY_SERVICE_BEAN_NAME,
                GlobalPropertyHistoryService.class);
    }

    private MessagesSchedulerService getMessagesSchedulerService() {
        return Context.getRegisteredComponent(CFLConstants.MESSAGES_SCHEDULER_SERVICE_BEAN_NAME,
                MessagesSchedulerService.class);
    }
}
