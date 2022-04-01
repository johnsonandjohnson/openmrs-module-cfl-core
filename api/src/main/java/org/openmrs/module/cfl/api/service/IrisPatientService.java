package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;

public interface IrisPatientService {

    /**
     * Saves new or updates already existing patient
     *
     * @param patient - related patient
     * @return saved or updated patient
     */
    Patient saveOrUpdatePatient(Patient patient);
}
