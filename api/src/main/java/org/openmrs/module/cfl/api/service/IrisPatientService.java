package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;

public interface IrisPatientService {
    Patient savePatient(Patient patient);

    /**
     * Updates already existing patient
     *
     * @param patient - related patient which already exists in the database
     * @return updated patient
     */
    Patient updatePatient(Patient patient);
}
