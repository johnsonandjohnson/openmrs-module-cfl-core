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

  Long getCountOfPatientsInLocations(String query, Set<String> locationUuids);

  Long getCountOfPatientsInLocations(String query, boolean includeVoided, Set<String> locationUuids);
}
