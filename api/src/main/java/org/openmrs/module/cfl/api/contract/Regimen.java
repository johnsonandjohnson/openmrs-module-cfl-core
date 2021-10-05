package org.openmrs.module.cfl.api.contract;

import java.util.List;
import java.util.Map;

public class Regimen {

    private  String syncScope;

    private long operatorCredentialsRetentionTime;

    private long operatorOfflineSessionTimeout;

    private List<Vaccine> vaccine;

    private List<Manufacturer> manufacturers;

    private List<PersonLanguages> personLanguages;

    private List<AuthSteps> authSteps;

    private String irisScore;

    private Map<String, List<AddressFields>> addressFields;

    private boolean allowManualParticipantIDEntry;

    public String getSyncScope() {
        return syncScope;
    }

    public void setSyncScope(String syncScope) {
        this.syncScope = syncScope;
    }

    public long getOperatorCredentialsRetentionTime() {
        return operatorCredentialsRetentionTime;
    }

    public void setOperatorCredentialsRetentionTime(long operatorCredentialsRetentionTime) {
        this.operatorCredentialsRetentionTime = operatorCredentialsRetentionTime;
    }

    public long getOperatorOfflineSessionTimeout() {
        return operatorOfflineSessionTimeout;
    }

    public void setOperatorOfflineSessionTimeout(long operatorOfflineSessionTimeout) {
        this.operatorOfflineSessionTimeout = operatorOfflineSessionTimeout;
    }

    public List<Vaccine> getVaccine() {
        return vaccine;
    }

    public void setVaccines(List<Vaccine> vaccine) {
        this.vaccine = vaccine;
    }

    public List<Manufacturer> getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(List<Manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
    }

    public List<PersonLanguages> getPersonLanguages() {
        return personLanguages;
    }

    public void setPersonLanguages(List<PersonLanguages> personLanguages) {
        this.personLanguages = personLanguages;
    }

    public List<AuthSteps> getAuthSteps() {
        return authSteps;
    }

    public void setAuthSteps(List<AuthSteps> authSteps) {
        this.authSteps = authSteps;
    }

    public String getIrisScore() {
        return irisScore;
    }

    public void setIrisScore(String irisScore) {
        this.irisScore = irisScore;
    }

    public Map<String, List<AddressFields>> getAddressFields() {
        return addressFields;
    }

    public void setAddressFields(Map<String, List<AddressFields>> addressFields) {
        this.addressFields = addressFields;
    }

    public boolean isAllowManualParticipantIDEntry() {
        return allowManualParticipantIDEntry;
    }

    public void setAllowManualParticipantIDEntry(boolean allowManualParticipantIDEntry) {
        this.allowManualParticipantIDEntry = allowManualParticipantIDEntry;
    }
}
