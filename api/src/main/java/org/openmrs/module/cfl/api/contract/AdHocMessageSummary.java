package org.openmrs.module.cfl.api.contract;

public final class AdHocMessageSummary {
    private final int totalNumberOfPatients;

    public AdHocMessageSummary(int totalNumberOfPatients) {
        this.totalNumberOfPatients = totalNumberOfPatients;
    }

    public int getTotalNumberOfPatients() {
        return totalNumberOfPatients;
    }
}
