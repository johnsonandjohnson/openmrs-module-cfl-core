/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.Collections;
import java.util.List;

/**
 * The VaccinationEncounterListener class.
 * <p>
 * This listener is responsible for scheduling following visits according to the vaccination program defined in Global
 * Property {@link CFLConstants#VACCINATION_PROGRAM_KEY}.
 * </p>
 * <p>
 * This listener must be enabled via Global Parameter: {@link CFLConstants#VACCINATION_LISTENER_KEY}.
 * </p>
 * <p>
 * The following visits are scheduled based on the Encounters date.
 * </p>
 * <p>
 * The listener observes the creation of an Encounter event and runs it's logic only when:
 * <ul>
 *     <li>the Encounter type is defined in Global Property
 *     {@link CFLConstants#VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_KEY}</li>
 *     <li>the Encounter has visit assigned</li>
 *     <li>the Vaccination information is enabled ({@link CFLConstants#VACCINATION_INFORMATION_ENABLED_KEY} is true) </li>
 *     <li>the Visit has occurred status</li>
 *     <li>the Visit is the last dosage visit scheduled for its patient</li>
 * </ul>
 * </p>
 *
 * @see UpdatingVisitListener
 */
public class VaccinationEncounterListener extends EncounterActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaccinationEncounterListener.class);

    @Override
    public List<String> subscribeToActions() {
        return Collections.singletonList(Event.Action.CREATED.name());
    }

    @Override
    public void performAction(Message message) {
        // is listener not enabled or is vaccination info not enabled
        if (!getConfigService().isVaccinationListenerEnabled(CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME) ||
                !getConfigService().isVaccinationInfoIsEnabled()) {
            LOGGER.info(
                    "Creation of future vaccination visits on encounter create by VaccinationEncounterListener has been " +
                            "disabled (global parameters: cfl.vaccination.listener or cfl.vaccinationInformationEnabled).");
            return;
        }

        final Encounter newEncounter = extractEncounter(message);

        if (isVaccinationEncounter(newEncounter) && newEncounter.getVisit() != null) {
            final Visit updatedVisit = newEncounter.getVisit();
            final String visitStatus = VisitUtil.getVisitStatus(updatedVisit);

            if (visitStatus.equals(VisitUtil.getOccurredVisitStatus())) {
                Context
                        .getService(VaccinationService.class)
                        .createFutureVisits(updatedVisit, newEncounter.getEncounterDatetime());
            }
        }
    }

    private boolean isVaccinationEncounter(Encounter newEncounter) {
        return getConfigService().getVaccinationEncounterTypeUUIDs().contains(newEncounter.getEncounterType().getUuid());
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }
}
