package org.openmrs.module.cfl.db;

import java.util.List;

/**
 * The ExtendedPatientDataDAO class.
 *
 * Provides additional custom methods related to vaccines functionalities.
 */
public interface ExtendedPatientDataDAO {

    /**
     * Returns all different vaccine names linked with any patient.
     *
     * @return list of different vaccine names linked with any patient
     */
    List<String> getVaccineNamesLinkedToAnyPatient();
}
