package org.openmrs.module.cfl.db;

import java.util.List;
import java.util.Set;

/**
 * The ExtendedPatientDataDAO class.
 *
 * <p>Provides additional custom methods related to vaccines functionality.
 */
public interface ExtendedPatientDataDAO {

  /**
   * Returns all different vaccine names linked with any patient.
   *
   * @return list of different vaccine names linked with any patient
   */
  List<String> getVaccineNamesLinkedToAnyPatient();

  /**
   * Gets the count of non-voided Patients which are filtered by an OpenMRS {@code query} and are
   * assigned to any Location with one of {@code locationUuids}.
   *
   * @param query the OpenMRS query, not null
   * @param locationUuids the List of Location Uuids, not null
   * @return the count of patients, never null
   * @see org.openmrs.api.PatientService#getCountOfPatients(String)
   */
  Long getCountOfPatientsInLocations(String query, Set<String> locationUuids);

  /**
   * Gets the count of Patients which are filtered by an OpenMRS {@code query}, and are assigned to
   * any Location with one of {@code locationUuids}. The {@code includeVoided} defines whether to
   * include voided Patients into the count or not.
   *
   * @param query the OpenMRS query, not null
   * @param includeVoided the flag to control whether to include voided patients or not
   * @param locationUuids the List of Location Uuids, not null
   * @return the count of patients, never null
   * @see org.openmrs.api.PatientService#getCountOfPatients(String)
   */
  Long getCountOfPatientsInLocations(
      String query, boolean includeVoided, Set<String> locationUuids);
}
