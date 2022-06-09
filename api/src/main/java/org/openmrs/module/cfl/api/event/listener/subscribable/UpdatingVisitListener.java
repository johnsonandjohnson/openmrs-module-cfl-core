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

import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.util.LockByKeyUtil;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.Collections;
import java.util.List;

/**
 * The UpdatingVisitListener class.
 *
 * <p>This listener is responsible for scheduling following visits according to the vaccination
 * program defined in Global Property {@link CFLConstants#VACCINATION_PROGRAM_KEY}.
 *
 * <p>This listener must be enabled via Global Parameter: {@link
 * CFLConstants#VACCINATION_LISTENER_KEY}.
 *
 * <p>The following visits are scheduled based on the Visits start date.
 *
 * <p>The listener observes the update of an Visit event and runs it's logic only when:
 *
 * <ul>
 *   <li>the Vaccination information is enabled ({@link
 *       CFLConstants#VACCINATION_INFORMATION_ENABLED_KEY} is true)
 *   <li>the Visit has occurred status
 *   <li>the Visit is the last dosage visit scheduled for its patient
 * </ul>
 *
 * @see VaccinationEncounterListener
 */
public class UpdatingVisitListener extends VisitActionListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdatingVisitListener.class);
  private static final LockByKeyUtil LOCK_BY_KEY = new LockByKeyUtil();

  @Override
  public List<String> subscribeToActions() {
    return Collections.singletonList(Event.Action.UPDATED.name());
  }

  @Override
  public void performAction(Message message) {
    // is listener not enabled or is vaccination info not enabled
    if (!getConfigService()
            .isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME)
        || !getConfigService().isVaccinationInfoIsEnabled()) {
      LOGGER.info(
          "Creation of future vaccination visits on visit update by UpdatingVisitListener has been disabled "
              + "(global parameters: cfl.vaccination.listener or cfl.vaccinationInformationEnabled).");
      return;
    }

    final String visitUuid = getVisitUuid(message);

    LOCK_BY_KEY.lock(visitUuid);

    try {
      final Visit updatedVisit = getVisit(visitUuid);
      final String visitStatus = VisitUtil.getVisitStatus(updatedVisit);

      if (visitStatus.equals(VisitUtil.getOccurredVisitStatus())) {
        Context.getService(VaccinationService.class)
            .createFutureVisits(updatedVisit, updatedVisit.getStartDatetime());
      }
    } finally {
      LOCK_BY_KEY.unlock(visitUuid);
    }
  }

  private ConfigService getConfigService() {
    return Context.getRegisteredComponent(
        CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
  }
}
