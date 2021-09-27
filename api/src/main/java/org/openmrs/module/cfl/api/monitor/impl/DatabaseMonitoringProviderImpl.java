package org.openmrs.module.cfl.api.monitor.impl;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cfl.api.monitor.MonitoredComponentStatusData;
import org.openmrs.module.cfl.api.monitor.MonitoringStatus;
import org.openmrs.module.cfl.api.monitor.ComponentMonitoringProvider;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class DatabaseMonitoringProviderImpl implements ComponentMonitoringProvider {
    private static final String COMPONENT_NAME = "Database";

    private static final String CHECK_SQL_QUERY = "SELECT 1";

    private DbSessionFactory sessionFactory;

    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Transactional(readOnly = true)
    @Override
    public MonitoredComponentStatusData getStatus() {
        try {
            sessionFactory.getCurrentSession().createSQLQuery(CHECK_SQL_QUERY).uniqueResult();
            return new MonitoredComponentStatusData(new Date(), MonitoringStatus.OK, null);
        } catch (final Exception ex) {
            return MonitoredComponentStatusData.fromException(ex);
        }
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
