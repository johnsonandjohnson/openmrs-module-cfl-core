package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.IrisPatientService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class IrisPatientServiceImpl extends BaseOpenmrsService implements IrisPatientService {

    private PatientService patientService;

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Patient savePatient(Patient patient) {
       return patientService.savePatient(patient);
    }
}
