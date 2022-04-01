package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.IrisPatientService;

public class IrisPatientServiceImpl extends BaseOpenmrsService implements IrisPatientService {

    private DbSessionFactory dbSessionFactory;
    private EntitySaveTransactionalWrapperService entitySaveTransactionalWrapperService;
    private final Log log;

    /**
     * Default constructor
     *
     * LogFactory used to get log
     */
    public IrisPatientServiceImpl() {
        this(LogFactory.getLog(IrisPatientServiceImpl.class));
    }

    /**
     * Constructor to facilitate unit tests.
     *
     * @param log log instance to use by this object
     */
    IrisPatientServiceImpl(Log log) {
        this.log = log;
    }

    @Override
    public Patient saveOrUpdatePatient(Patient patient) {
        if (patient.getId() == null) {
            log.debug("New patient will be saved");
        } else {
            log.debug("Patient with id: " + patient.getId() + " will be updated");
        }
        try {
            entitySaveTransactionalWrapperService.saveOrUpdatePatientInNewTransaction(patient);
        } finally {
            // It was either saved or not in #saveOrUpdatePatientInNewTransaction, we don't want any
            // following attempts to save it again in Db
            if (patient.getId() != null) {
                dbSessionFactory.getCurrentSession().evict(patient);
            }
        }
        return (Patient) dbSessionFactory.getCurrentSession().get(Patient.class, patient.getId());
    }

    public void setEntitySaveTransactionalWrapperService(
            EntitySaveTransactionalWrapperService entitySaveTransactionalWrapperService) {
        this.entitySaveTransactionalWrapperService = entitySaveTransactionalWrapperService;
    }

    public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
        this.dbSessionFactory = dbSessionFactory;
    }
}
