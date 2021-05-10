package org.openmrs.module.cfl.api.contract;

import com.google.gson.annotations.SerializedName;

public class CountrySetting {

    @SerializedName("SMS")
    private String sms;

    @SerializedName("CALL")
    private String call;

    private boolean performCallOnPatientRegistration;

    private boolean sendSmsOnPatientRegistration;

    private boolean shouldSendReminderViaCall;

    private boolean shouldSendReminderViaSms;

    private boolean shouldCreateFirstVisit;

    private boolean shouldCreateFutureVisit;

    private String allowOnPatientRegistrationTimeFrom;

    private String allowOnPatientRegistrationTimeTo;

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
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

    public String getAllowOnPatientRegistrationTimeFrom() {
        return allowOnPatientRegistrationTimeFrom;
    }

    public void setAllowOnPatientRegistrationTimeFrom(String allowOnPatientRegistrationTimeFrom) {
        this.allowOnPatientRegistrationTimeFrom = allowOnPatientRegistrationTimeFrom;
    }

    public String getAllowOnPatientRegistrationTimeTo() {
        return allowOnPatientRegistrationTimeTo;
    }

    public void setAllowOnPatientRegistrationTimeTo(String allowOnPatientRegistrationTimeTo) {
        this.allowOnPatientRegistrationTimeTo = allowOnPatientRegistrationTimeTo;
    }
}
