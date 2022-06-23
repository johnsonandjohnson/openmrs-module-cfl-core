/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.db;

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
