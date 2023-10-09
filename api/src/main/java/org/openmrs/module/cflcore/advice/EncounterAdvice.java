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
import org.openmrs.Order;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.db.ExtendedOrderDAO;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Modifies logic of EncounterService to fit SHIP CFL requirements. */
public class EncounterAdvice implements AfterReturningAdvice {
  private final Map<Method, Consumer<Encounter>> encounterMethodCallbacks = new HashMap<>();

  public EncounterAdvice() {
    try {
      encounterMethodCallbacks.put(
          EncounterService.class.getMethod("voidEncounter", Encounter.class, String.class),
          this::clearPreviousOrderStoppedDate);
    } catch (NoSuchMethodException nsme) {
      throw new IllegalStateException(
          "EncounterAdvice implementation incompatible with EncounterService.", nsme);
    }
  }

  @Override
  public void afterReturning(Object o, Method method, Object[] objects, Object o1) {
    if (encounterMethodCallbacks.containsKey(method)) {
      encounterMethodCallbacks.get(method).accept((Encounter) objects[0]);
    }
  }

  private void clearPreviousOrderStoppedDate(Encounter encounter) {
    for (Order encounterOrder : encounter.getOrders()) {
      final Order previousOrder = encounterOrder.getPreviousOrder();

      if (previousOrder == null || previousOrder.getVoided()) {
        continue;
      }

      getExtendedOrderDAO().restartOrder(previousOrder);
    }
  }

  private ExtendedOrderDAO getExtendedOrderDAO() {
    final List<ExtendedOrderDAO> implementations =
        Context.getRegisteredComponents(ExtendedOrderDAO.class);

    if (implementations.isEmpty()) {
      throw new IllegalStateException("Missing an ExtendedOrderDAO Spring bean.");
    }

    return implementations.get(0);
  }
}
