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

public class CountrySetting {
  private String sms;
  private String whatsApp;
  private String call;
  private boolean performCallOnPatientRegistration;
  private boolean sendSmsOnPatientRegistration;
  private boolean sendWhatsAppOnPatientRegistration;
  private String patientNotificationTimeWindowFrom;
  private String patientNotificationTimeWindowTo;

  public CountrySetting() {}

  @SuppressWarnings({"checkstyle:ParameterNumber", "PMD.ExcessiveParameterList"})
  CountrySetting(
      String sms,
      String whatsApp,
      String call,
      boolean performCallOnPatientRegistration,
      boolean sendSmsOnPatientRegistration,
      boolean sendWhatsAppOnPatientRegistration,
      String patientNotificationTimeWindowFrom,
      String patientNotificationTimeWindowTo) {
    this.sms = sms;
    this.whatsApp = whatsApp;
    this.call = call;
    this.performCallOnPatientRegistration = performCallOnPatientRegistration;
    this.sendSmsOnPatientRegistration = sendSmsOnPatientRegistration;
    this.sendWhatsAppOnPatientRegistration = sendWhatsAppOnPatientRegistration;
    this.patientNotificationTimeWindowFrom = patientNotificationTimeWindowFrom;
    this.patientNotificationTimeWindowTo = patientNotificationTimeWindowTo;
  }

  public String getSms() {
    return sms;
  }

  public void setSms(String sms) {
    this.sms = sms;
  }

  public String getWhatsApp() {
    return whatsApp;
  }

  public void setWhatsApp(String whatsApp) {
    this.whatsApp = whatsApp;
  }

  public String getCall() {
    return call;
  }

  public void setCall(String call) {
    this.call = call;
  }

  public boolean isPerformCallOnPatientRegistration() {
    return performCallOnPatientRegistration;
  }

  public void setPerformCallOnPatientRegistration(boolean performCallOnPatientRegistration) {
    this.performCallOnPatientRegistration = performCallOnPatientRegistration;
  }

  public boolean isSendSmsOnPatientRegistration() {
    return sendSmsOnPatientRegistration;
  }

  public void setSendSmsOnPatientRegistration(boolean sendSmsOnPatientRegistration) {
    this.sendSmsOnPatientRegistration = sendSmsOnPatientRegistration;
  }

  public boolean isSendWhatsAppOnPatientRegistration() {
    return sendWhatsAppOnPatientRegistration;
  }

  public void setSendWhatsAppOnPatientRegistration(boolean sendWhatsAppOnPatientRegistration) {
    this.sendWhatsAppOnPatientRegistration = sendWhatsAppOnPatientRegistration;
  }

  public String getPatientNotificationTimeWindowFrom() {
    return patientNotificationTimeWindowFrom;
  }

  public void setPatientNotificationTimeWindowFrom(String patientNotificationTimeWindowFrom) {
    this.patientNotificationTimeWindowFrom = patientNotificationTimeWindowFrom;
  }

  public String getPatientNotificationTimeWindowTo() {
    return patientNotificationTimeWindowTo;
  }

  public void setPatientNotificationTimeWindowTo(String patientNotificationTimeWindowTo) {
    this.patientNotificationTimeWindowTo = patientNotificationTimeWindowTo;
  }
}
