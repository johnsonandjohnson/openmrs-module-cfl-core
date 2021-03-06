/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.constant;

/** The constants class with Country Properties. */
public final class CountryPropertyConstants {
  public static final String PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_NAME =
      "messages.patientNotificationTimeWindowFrom";
  public static final String PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_DESC =
      "The property configures the beginning of the time window during which system is allowed to send notifications to "
          + "patients.";

  public static final String PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_NAME =
      "messages.patientNotificationTimeWindowTo";
  public static final String PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_DESC =
      "The property configures the end of the time window during which system is allowed to send notifications to "
          + "patients.";

  public static final String SHOULD_SEND_REMINDER_VIA_SMS_PROP_NAME =
      "messages.shouldSendReminderViaSms";
  public static final String SHOULD_SEND_REMINDER_VIA_SMS_PROP_DESC =
      "The property configures whether the system should send Visit reminders via SMS. Value: true/false.";

  public static final String SHOULD_SEND_REMINDER_VIA_CALL_PROP_NAME =
      "messages.shouldSendReminderViaCall";
  public static final String SHOULD_SEND_REMINDER_VIA_CALL_PROP_DESC =
      "The property configures whether the system should deliver Visit reminders via Call. Value: true/false.";

  public static final String PERFORM_CALL_ON_PATIENT_REGISTRATION_PROP_NAME =
      "messages.performCallOnPatientRegistration";
  public static final String PERFORM_CALL_ON_PATIENT_REGISTRATION_PROP_DESC =
      "The property configures whether new patients should receive Welcome Message via call after they are register in the"
          + " system. Value: true/false";

  public static final String SMS_CONFIG_PROP_NAME = "messages.smsConfig";
  public static final String SMS_CONFIG_PROP_DESC =
      "The name of default SMS provider configuration.";

  public static final String CALL_CONFIG_PROP_NAME = "messages.callConfig";
  public static final String CALL_CONFIG_PROP_DESC =
      "The name of default Call provider configuration.";

  public static final String SEND_SMS_ON_PATIENT_REGISTRATION_PROP_NAME =
      "messages.sendSmsOnPatientRegistration";
  public static final String SEND_SMS_ON_PATIENT_REGISTRATION_PROP_DESC =
      "The property configures whether new patients should receive Welcome Message via SMS after they are "
          + "register in the system. Value: true/false.";

  public static final String SHOULD_CREATE_FIRST_VISIT_PROP_NAME = "visits.shouldCreateFirstVisit";
  public static final String SHOULD_CREATE_FIRST_VISIT_PROP_DESC =
      "The property configures whether the system should create first Visit for newly register patients automatically. "
          + "Value: true/false.";

  public static final String SHOULD_CREATE_FUTURE_VISIT_PROP_NAME =
      "visits.shouldCreateFutureVisit";
  public static final String SHOULD_CREATE_FUTURE_VISIT_PROP_DESC =
      "The property configures whether the system should create a new future Visit after an existing Visit is marked as "
          + "OCCURRED. Value: true/false.";

  private CountryPropertyConstants() {}
}
