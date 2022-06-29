/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.monitor.impl;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cflcore.api.monitor.ComponentMonitoringProvider;
import org.openmrs.module.cflcore.api.monitor.MonitoredComponentStatusData;
import org.openmrs.module.cflcore.api.monitor.MonitoringStatus;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.springframework.transaction.annotation.Transactional;

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
            return new MonitoredComponentStatusData(DateUtil.now(), MonitoringStatus.OK, null);
        } catch (final Exception ex) {
            return MonitoredComponentStatusData.fromException(ex);
        }
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
