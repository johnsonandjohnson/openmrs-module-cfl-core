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

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.service.VaccinationService;
import org.openmrs.module.cflcore.api.util.VisitUtil;
import org.springframework.aop.AfterReturningAdvice;
import java.lang.reflect.Method;
import java.util.Date;

public class UpdateEncounterAdvice implements AfterReturningAdvice {

  private static final String SAVE_ENCOUNTER_METHOD_NAME = "saveEncounter";

  @Override
  public void afterReturning(Object o, Method method, Object[] objects, Object o1) {
    if (SAVE_ENCOUNTER_METHOD_NAME.equals(method.getName())) {
      Encounter encounter = (Encounter) objects[0];
      updateEncounterVisit(encounter.getVisit());
      createFutureVisits(encounter);
    }
  }

  private void updateEncounterVisit(Visit encounterVisit) {
    if (encounterVisit != null) {
      encounterVisit.setDateChanged(new Date());
      encounterVisit.setChangedBy(Context.getAuthenticatedUser());
      Context.getVisitService().saveVisit(encounterVisit);
    }
  }

  private void createFutureVisits(Encounter newEncounter) {
    if (isVaccinationEncounter(newEncounter) && newEncounter.getVisit() != null) {
      final Visit updatedVisit = newEncounter.getVisit();
      final String visitStatus = VisitUtil.getVisitStatus(updatedVisit);

      if (visitStatus.equals(VisitUtil.getOccurredVisitStatus())) {
        Context.getService(VaccinationService.class)
            .createFutureVisits(updatedVisit, newEncounter.getEncounterDatetime());
      }
    }
  }

  private boolean isVaccinationEncounter(Encounter newEncounter) {
    return getConfigService()
        .getVaccinationEncounterTypeUUIDs()
        .contains(newEncounter.getEncounterType().getUuid());
  }

  private ConfigService getConfigService() {
    return Context.getService(ConfigService.class);
  }
}
