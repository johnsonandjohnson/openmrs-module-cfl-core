/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.scheduler.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.model.GlobalPropertyHistory;
import org.openmrs.module.cfl.api.service.GlobalPropertyHistoryService;
import org.openmrs.module.cfl.api.service.VaccinationService;
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
        return CFLConstants.UPDATE_REGIMEN_JOB_NAME;
    }

    @Override
    public Class getTaskClass() {
        return RegimenVisitsChangeJobDefinition.class;
    }

    private GlobalPropertyHistoryService getGlobalPropertyHistoryService() {
        return Context.getRegisteredComponent(CFLConstants.GLOBAL_PROPERTY_HISTORY_SERVICE_BEAN_NAME,
                GlobalPropertyHistoryService.class);
    }

    private VaccinationService getVaccinationService() {
        return Context.getRegisteredComponent(CFLConstants.VACCINATION_SERVICE_BEAN_NAME, VaccinationService.class);
    }
}

