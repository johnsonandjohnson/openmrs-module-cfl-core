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

import org.openmrs.Order;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.springframework.aop.MethodBeforeAdvice;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * Advice class to override default behaviour of saving orders (Medicine Refill Visit Note). If you
 * try to save past refill note with date that overlaps another refill it throws exception and form
 * saving fails. This is workaround that sets date_activated to first valid date. Example: you
 * already have order from 1st of November to 1st of December. Now you want to save new order for
 * e.g. 20th of November - it fails. Below code will set this new order from 2nd of December (first
 * valid date).
 */
public class SaveOrderAdvice implements MethodBeforeAdvice {
  @Override
  public void before(Method method, Object[] objects, Object o) throws Throwable {
    if (method.getName().equalsIgnoreCase("saveOrder")) {
      Order currentOrder = (Order) objects[0];
      OrderService orderService = Context.getOrderService();
      Optional<Date> latestExpiredDate =
          orderService.getAllOrdersByPatient(currentOrder.getPatient()).stream()
              .filter(order -> order.getDateStopped() == null)
              .map(Order::getAutoExpireDate)
              .filter(Objects::nonNull)
              .filter(autoExpireDate -> autoExpireDate.after(currentOrder.getDateActivated()))
              .max(Date::compareTo);

      if (latestExpiredDate.isPresent()) {
        Date nearestValidActivatedDate =
            DateUtil.getDateWithTimeOfDay(
                DateUtil.addDaysToDate(latestExpiredDate.get(), 1),
                DateUtil.MIDNIGHT_TIME,
                DateUtil.getDefaultSystemTimeZone());
        currentOrder.setDateActivated(nearestValidActivatedDate);
      }
    }
  }
}
