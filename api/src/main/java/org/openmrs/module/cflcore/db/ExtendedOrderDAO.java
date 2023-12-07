/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.db;

import org.openmrs.Order;
import org.openmrs.Patient;

import java.util.Date;
import java.util.List;

/**
 * The ExtendedOrderDAO, contains additional DB operations related to Order entity.
 *
 * @see org.openmrs.api.db.OrderDAO
 */
public interface ExtendedOrderDAO {
  /**
   * Get a list of active orders for given {@code patient} and which ware created with non coded
   * reason other then {@code nonCodedReason}. An active order, is an non-voided order which has no
   * stopped date set.
   *
   * @param patient a patient, not null
   * @param nonCodedReason a reason, not null
   * @return a list of orders, not null
   */
  List<Order> getActiveOrdersWithOtherNonCodedReason(Patient patient, String nonCodedReason);

  /**
   * Stops an order. This method bypasses the common OpenMRS Orders logic, and just sets the date
   * stopped of an order, without any additional side-effects.
   *
   * @param order the order to stop, not null
   * @param stopDate the date to set as stopped date, not null
   * @return the update order, not null
   */
  Order stopOrder(Order order, Date stopDate);

  /**
   * Restarts an order. This method bypasses the common OpenMRS Orders logic, and just sets the date
   * stopped of an order to null, without any additional side-effects.
   *
   * @param order the order to restart, not null
   * @return the updated order, not null
   */
  Order restartOrder(Order order);
}
