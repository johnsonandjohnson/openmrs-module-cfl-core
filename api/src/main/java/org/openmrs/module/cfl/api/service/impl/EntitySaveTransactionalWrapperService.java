package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class EntitySaveTransactionalWrapperService {
  private VisitService visitService;
  private PatientService patientService;
  private DbSessionFactory dbSessionFactory;

  public void setPatientService(PatientService patientService) {
    this.patientService = patientService;
  }

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

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void saveOrUpdatePatientInNewTransaction(Patient patient) {
    final Patient mergedIntoSession =
            patient.getId() != null ? (Patient) dbSessionFactory.getCurrentSession().merge(patient) : patient;

    patientService.savePatient(mergedIntoSession);
  }
}
