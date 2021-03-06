/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service;

import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;

import java.util.List;

/** The Patient service with functions related to the Connect for Life. */
public interface CFLPatientService {
  /**
   * Finds non-voided Patients with assigned {@code vaccinationName} vaccination schedule.
   *
   * <p>The vaccination is assigned via Person Attribute with name: {@link
   * org.openmrs.module.cflcore.CFLConstants#VACCINATION_PROGRAM_ATTRIBUTE_NAME}.
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
