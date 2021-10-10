package org.openmrs.module.cfl.api.dto;

/**
 * The RegimensPatientsDataDTO class - represents required data for regimens configuration UI.
 */
public class RegimensPatientsDataDTO {

    private String regimenName;

    private Boolean isAnyPatientLinkedWithRegimen;

    public RegimensPatientsDataDTO() {
    }

    public RegimensPatientsDataDTO(String regimenName, boolean isAnyPatientLinkedWithRegimen) {
        this.regimenName = regimenName;
        this.isAnyPatientLinkedWithRegimen = isAnyPatientLinkedWithRegimen;
    }

    public String getRegimenName() {
        return regimenName;
    }

    public void setRegimenName(String regimenName) {
        this.regimenName = regimenName;
    }

    public Boolean isAnyPatientLinkedWithRegimen() {
        return isAnyPatientLinkedWithRegimen;
    }

    public void setAnyPatientLinkedWithRegimen(Boolean anyPatientLinkedWithRegimen) {
        isAnyPatientLinkedWithRegimen = anyPatientLinkedWithRegimen;
    }
}
