package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;

import java.util.List;

/** The Patient service with functions related to the Connect for Life. */
public interface CFLPatientService {
  /**
   * Finds non-voided Patients with assigned {@code vaccinationName} vaccination schedule.
   *
   * <p>The vaccination is assigned via Person Attribute with name: {@link
   * org.openmrs.module.cfl.CFLConstants#VACCINATION_PROGRAM_ATTRIBUTE_NAME}.
   *
   * @return the List of Patients with given vaccination, never null
   */
  List<Patient> findByVaccinationName(String vaccinationName);

  /**
   * Gets the count of not-voided Patients.
   *
   * <p>Compared to {@link org.openmrs.api.PatientService#getCountOfPatients(String)}, this method
   * applies Location Based Access logic.
   *
   * @param query the OpenMRS query, not null
   * @return the count of not-voided Patients, never null
   */
  @Authorized({"Get Patients"})
  Long getCountOfPatients(String query);

  /**
   * Gets the count of Patients, optionally including voided Patients.
   *
   * <p>Compared to {@link org.openmrs.api.PatientService#getCountOfPatients(String,boolean)}, this
   * method applies Location Based Access logic.
   *
   * @param query the OpenMRS query, not null
   * @param includeVoided the flag whether to include voided Patients, or not
   * @return the count of Patients, never null
   */
  @Authorized({"Get Patients"})
  Long getCountOfPatients(String query, boolean includeVoided);
}
