package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;

public interface IrisPatientService {
    Patient savePatient(Patient patient);

    Patient updatePatient(Patient patient);
}
