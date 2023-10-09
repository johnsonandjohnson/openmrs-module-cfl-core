/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.db.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.module.cflcore.db.ExtendedOrderDAO;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ExtendedOrderDAOImpl implements ExtendedOrderDAO {
  private static final String ACTIVE_ORDER_IDS_BY_NON_CODED_REASON_SQL =
      "SELECT o.order_id "
          + "FROM orders o "
          + "WHERE o.voided = 0 and o.patient_id = :patientId and o.date_stopped is null "
          + "and o.order_reason_non_coded != :nonCodedReason";

  private SessionFactory sessionFactory;

  @Override
  public List<Order> getActiveOrdersWithOtherNonCodedReason(
      Patient patient, String nonCodedReason) {
    final Session currentSession = sessionFactory.getCurrentSession();
    final Query activeOrdersByNonCodedReasonQuery =
        currentSession
            .createSQLQuery(ACTIVE_ORDER_IDS_BY_NON_CODED_REASON_SQL)
            .setInteger("patientId", patient.getId())
            .setString("nonCodedReason", nonCodedReason);

    return (List<Order>) activeOrdersByNonCodedReasonQuery.list().stream()
        .map(orderId -> currentSession.get(Order.class, (Number) orderId))
        .collect(Collectors.toList());
  }

  @Override
  public Order stopOrder(Order order, Date stopDate) {
    setOrderProperty(order, "dateStopped", stopDate);
    sessionFactory.getCurrentSession().saveOrUpdate(order);
    return order;
  }

  @Override
  public Order restartOrder(Order order) {
    setOrderProperty(order, "dateStopped", null);
    sessionFactory.getCurrentSession().saveOrUpdate(order);
    return order;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private void setOrderProperty(Order order, String propertyName, Object value) {
    try {
      final Field field = Order.class.getDeclaredField(propertyName);
      field.setAccessible(true);
      field.set(order, value);
    } catch (Exception var10) {
      throw new APIException(
          "Order.failed.set.property", new Object[] {propertyName, order}, var10);
    }
  }
}
