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

import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class EntitySaveTransactionalWrapperService {
  private VisitService visitService;
  private DbSessionFactory dbSessionFactory;

  public void setVisitService(VisitService visitService) {
    this.visitService = visitService;
  }

  public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void saveOrUpdateVisitInNewTransaction(Visit visit) {
    final Visit mergedIntoSession =
        visit.getId() != null ? (Visit) dbSessionFactory.getCurrentSession().merge(visit) : visit;

    visitService.saveVisit(mergedIntoSession);
  }
}
