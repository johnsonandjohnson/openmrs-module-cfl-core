/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.Visit;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.IrisVisitService;

public class IrisVisitServiceImpl extends BaseOpenmrsService implements IrisVisitService {

  private EntitySaveTransactionalWrapperService entitySaveTransactionalWrapperService;
  private DbSessionFactory dbSessionFactory;

  public void setEntitySaveTransactionalWrapperService(
      EntitySaveTransactionalWrapperService entitySaveTransactionalWrapperService) {
    this.entitySaveTransactionalWrapperService = entitySaveTransactionalWrapperService;
  }

  public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  @Override
  public Visit saveVisit(Visit visit) {
    try {
      entitySaveTransactionalWrapperService.saveOrUpdateVisitInNewTransaction(visit);
    } finally {
      // It was either saved or not in #saveOrUpdateVisitInNewTransaction, we don't want any
      // following attempts to save it again in Db
      if (visit.getId() != null) {
        dbSessionFactory.getCurrentSession().evict(visit);
      }
    }

    return (Visit) dbSessionFactory.getCurrentSession().get(Visit.class, visit.getId());
  }
}
