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
import org.openmrs.module.cflcore.api.contract.AdHocMessageSummary;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/** Ad Hoc message service. */
public interface AdHocMessageService {

  /**
   * Schedules an Ad Hoc message. For each of the {@code patients} and each of the {@code
   * channelTypes}, a single Scheduled Service Group and single Scheduled Service is created. The
   * one {@code channelConfiguration} is passed as-is to all Scheduled Service, the same for all
   * channel types. The Message delivery Job is scheduled as in the usual message handling.
   *
   * @param deliveryDateTime the date time in system's time, not null
   * @param channelTypes channel types to send messages to, not null
   * @param channelConfiguration the shared channel configuration, not null
   * @param patients the collection of recipients, not null
   * @return a summary of the actual scheduled messages
   */
  AdHocMessageSummary scheduleAdHocMessage(
      final Date deliveryDateTime,
      final Set<String> channelTypes,
      final Map<String, String> channelConfiguration,
      final Collection<Patient> patients);
}
