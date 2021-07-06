package org.openmrs.module.cfl.api.scheduler.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.model.GlobalPropertyHistory;
import org.openmrs.module.cfl.api.service.GlobalPropertyHistoryService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.messages.api.scheduler.job.JobDefinition;

import java.util.Optional;

public class RegimenVisitsChangeJobDefinition extends JobDefinition {

    private static final Log LOGGER = LogFactory.getLog(RegimenVisitsChangeJobDefinition.class);

    @Override
    public void execute() {
        LOGGER.info(getTaskName() + " started");
        Optional<GlobalPropertyHistory> previousGP = getGlobalPropertyHistoryService()
                .getPreviousValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        String previousGPValue = previousGP.map(GlobalPropertyHistory::getPropertyValue).orElse(null);

        String currentGPValue = Context.getAdministrationService()
                .getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);

        if (previousGPValue != null && currentGPValue != null) {
            getVaccinationService().rescheduleVisitsBasedOnRegimenChanges(previousGPValue, currentGPValue);
        }
    }

    @Override
    public boolean shouldStartAtFirstCreation() {
        return false;
    }

    @Override
    public String getTaskName() {
        return CFLConstants.UPDATE_REGIMEN_TASK_NAME_PREFIX + getTaskExecutionDate();
    }

    @Override
    public Class getTaskClass() {
        return RegimenVisitsChangeJobDefinition.class;
    }

    private String getTaskExecutionDate() {
        Optional<GlobalPropertyHistory> vaccinesGPHistory = getGlobalPropertyHistoryService()
                .getPreviousValueOfGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        return vaccinesGPHistory.map(globalPropertyHistory ->
                DateUtil.convertDate(DateUtil.getDateWithTimeOfDay(
                        DateUtil.addDaysToDate(globalPropertyHistory.getActionDate(), 1), DateUtil.MIDNIGHT_TIME,
                        DateUtil.getSystemTimeZone()), DateUtil.DATE_AND_TIME_AND_TIME_ZONE_PATTERN))
                .orElse(null);
    }

    private GlobalPropertyHistoryService getGlobalPropertyHistoryService() {
        return Context.getRegisteredComponent(CFLConstants.GLOBAL_PROPERTY_HISTORY_SERVICE_BEAN_NAME,
                GlobalPropertyHistoryService.class);
    }

    private VaccinationService getVaccinationService() {
        return Context.getRegisteredComponent(CFLConstants.VACCINATION_SERVICE_BEAN_NAME, VaccinationService.class);
    }
}

