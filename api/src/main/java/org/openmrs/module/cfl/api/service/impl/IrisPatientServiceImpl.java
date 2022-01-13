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

    private static final Log LOGGER = LogFactory.getLog(IrisPatientServiceImpl.class);

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Patient savePatient(Patient patient) {
       return patientService.savePatient(patient);
    }

    public Patient updatePatient(Patient patient) {

        LOGGER.info("Patient with uuid: " + patient.getUuid() + "has not been updated");
        return patient;
    }
}
