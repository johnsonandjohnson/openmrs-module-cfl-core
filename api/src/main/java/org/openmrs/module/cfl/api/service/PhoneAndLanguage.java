package org.openmrs.module.cfl.api.service;

public class PhoneAndLanguage {

    private final String phoneNumber;
    private final String language;

    public PhoneAndLanguage(Object[] resultRow) {
        this.phoneNumber = (String) resultRow[0];
        this.language = (String) resultRow[1];
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLanguage() {
        return language;
    }
}
