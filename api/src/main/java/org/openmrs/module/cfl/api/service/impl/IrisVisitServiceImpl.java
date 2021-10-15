package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.IrisVisitService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class IrisVisitServiceImpl extends BaseOpenmrsService implements IrisVisitService {

    private VisitService visitService;

    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Visit saveVisit(Visit visit) {
        return visitService.saveVisit(visit);
    }
}
