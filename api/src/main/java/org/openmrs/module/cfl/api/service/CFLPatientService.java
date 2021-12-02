package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;

import java.util.List;

public interface CFLPatientService {

    List<Patient> findByVaccinationName(String vaccinationName);

    @Authorized({"Get Patients"})
    Long getCountOfPatients(String query);

    @Authorized({"Get Patients"})
    Long getCountOfPatients(String query, boolean includeVoided);
}
