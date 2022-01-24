package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.IrisPatientService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class IrisPatientServiceImpl extends BaseOpenmrsService implements IrisPatientService {

    private PatientService patientService;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Patient savePatient(Patient patient) {
        return patientService.savePatient(patient);
    }

    /**
     * Updates already existing patient.
     *
     * log.info used to get information about patient Uuid and status.
     */
    public Patient updatePatient(Patient patient) {
        log.info("Patient with uuid: " + patient.getUuid() + " has not been updated. Update feature is not supported yet.");
        return patient;
    }

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }
}
