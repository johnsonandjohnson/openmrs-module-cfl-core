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

/**
 * The Patient Visit Config service.
 *
 * <p>Provides methods related to visits configuration for patient.
 */
public interface PatientVisitConfigService {

  /**
   * Checks whether first visit should be created.
   *
   * @param patient patient for whom check is performed
   * @return true/false
   */
  boolean shouldCreateFirstVisit(Patient patient);

  /**
   * Checks whether future visits should be created.
   *
   * @param patient patient for whom check is performed
   * @return true/false
   */
  boolean shouldCreateFutureVisits(Patient patient);

  /**
   * Checks whether Visit reminder with SMS channel should be created.
   *
   * @param patient patient for whom check is performed
   * @return true/false
   */
  boolean shouldSendReminderViaSMS(Patient patient);

  /**
   * Checks whether Visit reminder with Call channel should be created.
   *
   * @param patient patient for whom check is performed
   * @return true/false
   */
  boolean shouldSendReminderViaCall(Patient patient);

  /**
   * Checks whether Visit reminder with WhatsApp channel should be created.
   *
   * @param patient patient for whom check is performed
   * @return true/false
   */
  boolean shouldSendReminderViaWhatsApp(Patient patient);
}
