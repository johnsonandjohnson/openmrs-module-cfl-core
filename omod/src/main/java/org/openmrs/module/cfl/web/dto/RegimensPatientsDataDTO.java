package org.openmrs.module.cfl.web.dto;

import java.util.List;

/**
 * The RegimensPatientsDataDTO class - represents required data for regimens configuration UI.
 */
public class RegimensPatientsDataDTO {

    private String regimenName;

    private List<String> patientUuids;

    private Integer numberOfParticipants;

    private Boolean isAnyPatientLinkedWithRegimen;

    public RegimensPatientsDataDTO() {

    }

    public RegimensPatientsDataDTO(String regimenName, List<String> patientUuids, Integer numberOfParticipants,
                                   boolean isAnyPatientLinkedWithRegimen) {
        this.regimenName = regimenName;
        this.patientUuids = patientUuids;
        this.numberOfParticipants = numberOfParticipants;
        this.isAnyPatientLinkedWithRegimen = isAnyPatientLinkedWithRegimen;
    }

    public String getRegimenName() {
        return regimenName;
    }

    public void setRegimenName(String regimenName) {
        this.regimenName = regimenName;
    }

    public List<String> getPatientUuids() {
        return patientUuids;
    }

    public void setPatientUuids(List<String> patientUuids) {
        this.patientUuids = patientUuids;
    }

    public Integer getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(Integer numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public Boolean isAnyPatientLinkedWithRegimen() {
        return isAnyPatientLinkedWithRegimen;
    }

    public void setAnyPatientLinkedWithRegimen(Boolean anyPatientLinkedWithRegimen) {
        isAnyPatientLinkedWithRegimen = anyPatientLinkedWithRegimen;
    }
}
