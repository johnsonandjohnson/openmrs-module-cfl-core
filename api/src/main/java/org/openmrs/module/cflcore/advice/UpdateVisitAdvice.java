/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.advice;

import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.service.VaccinationService;
import org.openmrs.module.cflcore.api.util.LockByKeyUtil;
import org.openmrs.module.cflcore.api.util.VisitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class UpdateVisitAdvice implements AfterReturningAdvice {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateVisitAdvice.class);

  private static final LockByKeyUtil LOCK_BY_KEY = new LockByKeyUtil();

  private static final List<String> SAVE_UPDATE_VISIT_METHOD_NAMES =
      Arrays.asList("saveVisit", "updateVisit");

  @Override
  public void afterReturning(Object o, Method method, Object[] objects, Object o1) {
    if (SAVE_UPDATE_VISIT_METHOD_NAMES.contains(method.getName())) {
      Object visitObject = objects[0];
      String visitUuid = null;
      if (visitObject instanceof Visit) {
        visitUuid = ((Visit) objects[0]).getUuid();
      } else if (visitObject instanceof String) {
        visitUuid = (String) visitObject;
      }

      createFutureVisits(visitUuid);
    }
  }

  private void createFutureVisits(String visitUuid) {
    // is listener not enabled or is vaccination info not enabled
    if (!getConfigService()
            .isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME)
        || !getConfigService().isVaccinationInfoIsEnabled()) {
      LOGGER.info(
          "Creation of future vaccination visits on visit update by UpdatingVisitListener has been disabled "
              + "(global parameters: cfl.vaccination.listener or cfl.vaccinationInformationEnabled).");
      return;
    }

    LOCK_BY_KEY.lock(visitUuid);

    try {
      final Visit updatedVisit = Context.getVisitService().getVisitByUuid(visitUuid);
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
    return Context.getService(ConfigService.class);
  }
}
