package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;

import java.util.List;

public interface CFLVisitService {

    void rescheduleVisitsByPatients(List<Patient> patients);
}
