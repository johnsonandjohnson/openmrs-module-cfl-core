package org.openmrs.module.cfl.api.service.impl;

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
