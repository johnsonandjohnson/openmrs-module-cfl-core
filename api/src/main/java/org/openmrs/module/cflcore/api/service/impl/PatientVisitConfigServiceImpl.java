/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.service.CustomAdministrationService;
import org.openmrs.module.cflcore.api.service.PatientVisitConfigService;
import org.openmrs.module.cflcore.api.util.GlobalPropertiesConstants;

public class PatientVisitConfigServiceImpl implements PatientVisitConfigService {

  @Override
  public boolean shouldCreateFirstVisit(Patient patient) {
    return getGPValue(GlobalPropertiesConstants.SHOULD_CREATE_FIRST_VISIT.getKey(), patient);
  }

  @Override
  public boolean shouldCreateFutureVisits(Patient patient) {
    return getGPValue(GlobalPropertiesConstants.SHOULD_CREATE_FUTURE_VISITS.getKey(), patient);
  }

  @Override
  public boolean shouldSendReminderViaSMS(Patient patient) {
    return getGPValue(GlobalPropertiesConstants.SHOULD_SEND_REMINDER_VIA_SMS.getKey(), patient);
  }

  @Override
  public boolean shouldSendReminderViaCall(Patient patient) {
    return getGPValue(GlobalPropertiesConstants.SHOULD_SEND_REMINDER_VIA_CALL.getKey(), patient);
  }

  @Override
  public boolean shouldSendReminderViaWhatsApp(Patient patient) {
    return getGPValue(
        GlobalPropertiesConstants.SHOULD_SEND_REMINDER_VIA_WHATSAPP.getKey(), patient);
  }

  private boolean getGPValue(String gpKey, Patient patient) {
    String gpValue =
        Context.getService(CustomAdministrationService.class).getGlobalProperty(gpKey, patient);

    return Boolean.parseBoolean(gpValue);
  }
}
