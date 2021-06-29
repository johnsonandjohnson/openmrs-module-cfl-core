package org.openmrs.module.cfl.api.service.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cfl.api.model.GlobalPropertyHistory;
import org.openmrs.module.cfl.api.service.GlobalPropertyHistoryService;
import org.springframework.transaction.annotation.Transactional;

public class GlobalPropertyHistoryServiceImpl implements GlobalPropertyHistoryService {

    private static final String PROPERTY_COLUMN_NAME = "property";

    private static final String ID_COLUMN_NAME = "id";

    private static final Integer ONE = 1;

    private DbSessionFactory sessionFactory;

    @Transactional(readOnly = true)
    @Override
    public String getLastValueOfGlobalProperty(String gpName) {
        Criteria criteria = getSession().createCriteria(GlobalPropertyHistory.class);
        criteria.add(Restrictions.eq(PROPERTY_COLUMN_NAME, gpName));
        criteria.addOrder(Order.desc(ID_COLUMN_NAME));
        criteria.setMaxResults(ONE);
        GlobalPropertyHistory globalPropertyHistory = (GlobalPropertyHistory) criteria.uniqueResult();

        return globalPropertyHistory.getPropertyValue();
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }
}
