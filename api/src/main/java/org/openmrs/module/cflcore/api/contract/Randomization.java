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

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class Randomization {
    private List<Vaccination> vaccinations;

    public Randomization(Vaccination[] vaccinations) {
        this.vaccinations = Arrays.asList(vaccinations);
    }

    public Vaccination findByVaccinationProgram(String vaccinationProgram) {
        Vaccination vaccination = null;
        for (Vaccination vacc : vaccinations) {
            if (StringUtils.equals(vacc.getName(), vaccinationProgram)) {
                vaccination = vacc;
                break;
            }
        }
        return vaccination;
    }

    public List<Vaccination> getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(List<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }
}
