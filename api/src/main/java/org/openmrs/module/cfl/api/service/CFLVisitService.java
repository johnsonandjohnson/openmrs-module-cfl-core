package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;

import java.util.List;

public interface CFLVisitService {

    /**
     * Reschedules (voids and recreates) future visits for list of patients.
     * This method is executed in separate transaction due dividing regimen update job logic into batches
     * to avoid memory issue with one big transaction in case of large number of patients.
     *
     * @param patients list of patients for whom visits are to be rescheduled
     */
    void rescheduleVisitsByPatients(List<Patient> patients);
}
