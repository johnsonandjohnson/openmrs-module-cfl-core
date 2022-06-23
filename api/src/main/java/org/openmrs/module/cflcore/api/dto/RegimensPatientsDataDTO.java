/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.dto;

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
