package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;

import java.util.List;

public interface CFLPatientService {

    List<Patient> findByVaccinationName(String vaccinationName);

    List<String> getVaccineNamesLinkedToAnyPatient();
}
