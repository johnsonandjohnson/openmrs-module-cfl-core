package org.openmrs.module.cfl.api.service.impl;

import org.hibernate.Transaction;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.service.IrisPatientService;

public class IrisPatientServiceImpl extends BaseOpenmrsService implements IrisPatientService {

    private DbSessionFactory dbSessionFactory;
    private PatientService patientService;

    public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
        this.dbSessionFactory = dbSessionFactory;
    }

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    public Patient savePatient(Patient patient) {
        Transaction tx = null;
        Patient result;
        try {
            tx = dbSessionFactory.getCurrentSession().getTransaction();
            if (tx != null && tx.isActive()) {
                tx.commit();
                dbSessionFactory.getCurrentSession().clear();
            }
            tx = dbSessionFactory.getCurrentSession().beginTransaction();
            result = patientService.savePatient(patient);
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
