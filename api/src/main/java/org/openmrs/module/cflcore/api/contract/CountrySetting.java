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
  private boolean shouldSendReminderViaCall;
  private boolean shouldSendReminderViaSms;
  private boolean sendWhatsAppOnPatientRegistration;
  private boolean shouldSendReminderViaWhatsApp;
  private boolean shouldCreateFirstVisit;
  private boolean shouldCreateFutureVisit;
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
      boolean shouldSendReminderViaCall,
      boolean shouldSendReminderViaSms,
      boolean sendWhatsAppOnPatientRegistration,
      boolean shouldSendReminderViaWhatsApp,
      boolean shouldCreateFirstVisit,
      boolean shouldCreateFutureVisit,
      String patientNotificationTimeWindowFrom,
      String patientNotificationTimeWindowTo) {
    this.sms = sms;
    this.whatsApp = whatsApp;
    this.call = call;
    this.performCallOnPatientRegistration = performCallOnPatientRegistration;
    this.sendSmsOnPatientRegistration = sendSmsOnPatientRegistration;
    this.shouldSendReminderViaCall = shouldSendReminderViaCall;
    this.shouldSendReminderViaSms = shouldSendReminderViaSms;
    this.sendWhatsAppOnPatientRegistration = sendWhatsAppOnPatientRegistration;
    this.shouldSendReminderViaWhatsApp = shouldSendReminderViaWhatsApp;
    this.shouldCreateFirstVisit = shouldCreateFirstVisit;
    this.shouldCreateFutureVisit = shouldCreateFutureVisit;
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

  public boolean isShouldSendReminderViaCall() {
    return shouldSendReminderViaCall;
  }

  public void setShouldSendReminderViaCall(boolean shouldSendReminderViaCall) {
    this.shouldSendReminderViaCall = shouldSendReminderViaCall;
  }

  public boolean isShouldSendReminderViaSms() {
    return shouldSendReminderViaSms;
  }

  public void setShouldSendReminderViaSms(boolean shouldSendReminderViaSms) {
    this.shouldSendReminderViaSms = shouldSendReminderViaSms;
  }

  public boolean isSendWhatsAppOnPatientRegistration() {
    return sendWhatsAppOnPatientRegistration;
  }

  public void setSendWhatsAppOnPatientRegistration(boolean sendWhatsAppOnPatientRegistration) {
    this.sendWhatsAppOnPatientRegistration = sendWhatsAppOnPatientRegistration;
  }

  public boolean isShouldSendReminderViaWhatsApp() {
    return shouldSendReminderViaWhatsApp;
  }

  public void setShouldSendReminderViaWhatsApp(boolean shouldSendReminderViaWhatsApp) {
    this.shouldSendReminderViaWhatsApp = shouldSendReminderViaWhatsApp;
  }

  public boolean isShouldCreateFirstVisit() {
    return shouldCreateFirstVisit;
  }

  public void setShouldCreateFirstVisit(boolean shouldCreateFirstVisit) {
    this.shouldCreateFirstVisit = shouldCreateFirstVisit;
  }

  public boolean isShouldCreateFutureVisit() {
    return shouldCreateFutureVisit;
  }

  public void setShouldCreateFutureVisit(boolean shouldCreateFutureVisit) {
    this.shouldCreateFutureVisit = shouldCreateFutureVisit;
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
