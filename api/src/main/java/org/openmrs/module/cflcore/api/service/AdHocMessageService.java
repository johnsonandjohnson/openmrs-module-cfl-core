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

public interface AdHocMessageService {

    AdHocMessageSummary scheduleAdHocMessage(final Date deliveryDateTime, final Set<String> channelTypes,
                                             final Map<String, String> messageProperties,
                                             final Collection<Patient> patients);
}
