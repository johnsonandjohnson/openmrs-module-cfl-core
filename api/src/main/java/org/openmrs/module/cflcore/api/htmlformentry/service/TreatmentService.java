/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.service;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.cflcore.api.htmlformentry.model.Treatment;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/** The TreatmentService service, handles patient's treatment program. */
public interface TreatmentService {

  /**
   * Gets all Treatment of given {@code patient}, including stopped and current, order by start
   * date.
   *
   * @param patient the patient, not null
   * @return a list of treatments, not null
   */
  List<Treatment> getAllTreatments(Patient patient);

  /**
   * Gets current Treatment of given {@code patient} relative to the {@code encounterDate}. In other
   * words, the method returns Treatment which, for given {@code patient}, was active at the {@code
   * encounterDate}.
   *
   * @param patient the patient, not null
   * @param encounterDate the date at which to get active treatment for, not null
   * @return a Treatment active at {@code encounterDate}
   */
  Optional<Treatment> getCurrentTreatment(Patient patient, Date encounterDate);

  /**
   * Voids any previous encounter relative to {@code encounter} of Program Form type.
   *
   * @param encounter the encounter to void previous encounters for, not null
   */
  void voidPreviousProgramEncounters(Encounter encounter);

  /**
   * Updates patient's program according to Program Form of {@code encounter}.
   *
   * @param encounter the encounter to update patient program from, not null
   */
  void updatePatientProgram(Encounter encounter);

  /**
   * Stops any ongoing Orders for {@code encounter}'s Patient. The Order's stopped date would be set
   * to the {@code encounter}'s date.
   *
   * @param encounter the encounter which should be used as a cause to stop Orders, not null
   */
  void stopOngoingDrugOrders(Encounter encounter);
}
