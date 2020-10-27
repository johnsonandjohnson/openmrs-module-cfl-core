package org.openmrs.module.cfl.api.service.impl;

import org.hibernate.Transaction;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.service.IrisVisitService;

public class IrisVisitServiceImpl  extends BaseOpenmrsService implements IrisVisitService {

    private DbSessionFactory dbSessionFactory;
    private VisitService visitService;

    public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
        this.dbSessionFactory = dbSessionFactory;
    }

    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    @Override
    public Visit saveVisit(Visit visit) {
        Transaction tx = null;
        Visit result;
        try {
            tx = dbSessionFactory.getCurrentSession().getTransaction();
            if (tx != null && tx.isActive()) {
                tx.rollback();
                dbSessionFactory.getCurrentSession().clear();
            }
            tx = dbSessionFactory.getCurrentSession().beginTransaction();

            result = visitService.saveVisit(visit);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            dbSessionFactory.getCurrentSession().clear();
            throw new CflRuntimeException(e);
        }
        return result;
    }
}
