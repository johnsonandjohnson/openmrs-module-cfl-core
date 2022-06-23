/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cflcore.api.model.GlobalPropertyHistory;
import org.openmrs.module.cflcore.api.service.GlobalPropertyHistoryService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class GlobalPropertyHistoryServiceImpl implements GlobalPropertyHistoryService {

    private static final Integer ONE = 1;

    private DbSessionFactory sessionFactory;

    @Transactional(readOnly = true)
    @Override
    public Optional<GlobalPropertyHistory> getPreviousValueOfGlobalProperty(String gpName) {
        Criteria criteria = getSession().createCriteria(GlobalPropertyHistory.class);
        criteria.add(Restrictions.eq(GlobalPropertyHistory.PROPERTY_FIELD_NAME, gpName));
        criteria.addOrder(Order.desc(GlobalPropertyHistory.ID_FIELD_NAME));
        criteria.setMaxResults(ONE);
        return Optional.ofNullable((GlobalPropertyHistory) criteria.uniqueResult());
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }
}
