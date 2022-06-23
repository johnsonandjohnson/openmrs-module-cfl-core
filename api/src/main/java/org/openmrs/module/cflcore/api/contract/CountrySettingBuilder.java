/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.contract;

import org.openmrs.module.cflcore.api.constant.CountryPropertyConstants;
import org.openmrs.module.messages.api.model.CountryProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.lang.Boolean.parseBoolean;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

public class CountrySettingBuilder {
  public static final Set<String> ALL_PROP_NAMES =
      unmodifiableSet(
          new HashSet<>(
              asList(
                  CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_NAME,
                  CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_NAME,
                  CountryPropertyConstants.SHOULD_SEND_REMINDER_VIA_SMS_PROP_NAME,
                  CountryPropertyConstants.PERFORM_CALL_ON_PATIENT_REGISTRATION_PROP_NAME,
                  CountryPropertyConstants.SMS_CONFIG_PROP_NAME,
                  CountryPropertyConstants.CALL_CONFIG_PROP_NAME,
                  CountryPropertyConstants.SEND_SMS_ON_PATIENT_REGISTRATION_PROP_NAME,
                  CountryPropertyConstants.SHOULD_SEND_REMINDER_VIA_CALL_PROP_NAME,
                  CountryPropertyConstants.SHOULD_CREATE_FIRST_VISIT_PROP_NAME,
                  CountryPropertyConstants.SHOULD_CREATE_FUTURE_VISIT_PROP_NAME)));

  private static final Map<String, BiConsumer<CountrySettingBuilder, String>>
      PROPERTY_SETTERS_MAPPING;

  static {
    final Map<String, BiConsumer<CountrySettingBuilder, String>> tmp = new HashMap<>();
    tmp.put(
        CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_NAME,
        CountrySettingBuilder::setPatientNotificationTimeWindowFrom);
    tmp.put(
        CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_NAME,
        CountrySettingBuilder::setPatientNotificationTimeWindowTo);
    tmp.put(
        CountryPropertyConstants.SHOULD_SEND_REMINDER_VIA_SMS_PROP_NAME,
        CountrySettingBuilder::setShouldSendReminderViaSms);
    tmp.put(
        CountryPropertyConstants.PERFORM_CALL_ON_PATIENT_REGISTRATION_PROP_NAME,
        CountrySettingBuilder::setPerformCallOnPatientRegistration);
    tmp.put(CountryPropertyConstants.SMS_CONFIG_PROP_NAME, CountrySettingBuilder::setSms);
    tmp.put(CountryPropertyConstants.CALL_CONFIG_PROP_NAME, CountrySettingBuilder::setCall);
    tmp.put(
        CountryPropertyConstants.SEND_SMS_ON_PATIENT_REGISTRATION_PROP_NAME,
        CountrySettingBuilder::setSendSmsOnPatientRegistration);
    tmp.put(
        CountryPropertyConstants.SHOULD_SEND_REMINDER_VIA_CALL_PROP_NAME,
        CountrySettingBuilder::setShouldSendReminderViaCall);
    tmp.put(
        CountryPropertyConstants.SHOULD_CREATE_FIRST_VISIT_PROP_NAME,
        CountrySettingBuilder::setShouldCreateFirstVisit);
    tmp.put(
        CountryPropertyConstants.SHOULD_CREATE_FUTURE_VISIT_PROP_NAME,
        CountrySettingBuilder::setShouldCreateFutureVisit);
    PROPERTY_SETTERS_MAPPING = Collections.unmodifiableMap(tmp);
  }

  private String sms;
  private String call;
  private boolean performCallOnPatientRegistration = false;
  private boolean sendSmsOnPatientRegistration = false;
  private boolean shouldSendReminderViaCall = false;
  private boolean shouldSendReminderViaSms = false;
  private boolean shouldCreateFirstVisit = false;
  private boolean shouldCreateFutureVisit = false;
  private String patientNotificationTimeWindowFrom = "10:00";
  private String patientNotificationTimeWindowTo = "18:00";

  /**
   * Adds Country Property to the settings. The builder automatically recognizes the property by
   * it's name.
   *
   * @param countryProperty the country property, not null
   * @return true, if property ahs been recognized, false otherwise
   */
  public boolean addCountryProperty(CountryProperty countryProperty) {
    final BiConsumer<CountrySettingBuilder, String> propertySetter =
        PROPERTY_SETTERS_MAPPING.get(countryProperty.getName());

    if (propertySetter != null) {
      propertySetter.accept(this, countryProperty.getValue());
      return true;
    } else {
      return false;
    }
  }

  public CountrySetting build() {
    return new CountrySetting(
        sms,
        call,
        performCallOnPatientRegistration,
        sendSmsOnPatientRegistration,
        shouldSendReminderViaCall,
        shouldSendReminderViaSms,
        shouldCreateFirstVisit,
        shouldCreateFutureVisit,
        patientNotificationTimeWindowFrom,
        patientNotificationTimeWindowTo);
  }

  private void setSms(String sms) {
    this.sms = sms;
  }

  private void setCall(String call) {
    this.call = call;
  }

  private void setPerformCallOnPatientRegistration(String performCallOnPatientRegistration) {
    this.performCallOnPatientRegistration = parseBoolean(performCallOnPatientRegistration);
  }

  private void setSendSmsOnPatientRegistration(String sendSmsOnPatientRegistration) {
    this.sendSmsOnPatientRegistration = parseBoolean(sendSmsOnPatientRegistration);
  }

  private void setShouldSendReminderViaCall(String shouldSendReminderViaCall) {
    this.shouldSendReminderViaCall = parseBoolean(shouldSendReminderViaCall);
  }

  private void setShouldSendReminderViaSms(String shouldSendReminderViaSms) {
    this.shouldSendReminderViaSms = parseBoolean(shouldSendReminderViaSms);
  }

  private void setShouldCreateFirstVisit(String shouldCreateFirstVisit) {
    this.shouldCreateFirstVisit = parseBoolean(shouldCreateFirstVisit);
  }

  private void setShouldCreateFutureVisit(String shouldCreateFutureVisit) {
    this.shouldCreateFutureVisit = parseBoolean(shouldCreateFutureVisit);
  }

  private void setPatientNotificationTimeWindowFrom(String patientNotificationTimeWindowFrom) {
    this.patientNotificationTimeWindowFrom = patientNotificationTimeWindowFrom;
  }

  private void setPatientNotificationTimeWindowTo(String patientNotificationTimeWindowTo) {
    this.patientNotificationTimeWindowTo = patientNotificationTimeWindowTo;
  }
}
