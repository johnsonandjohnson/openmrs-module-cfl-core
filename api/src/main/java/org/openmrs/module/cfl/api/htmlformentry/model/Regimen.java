/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.htmlformentry.model;

import java.util.List;

public class Regimen {

    private String name;

    private List<RegimenDrug> regimenDrugs;

    public Regimen(String name, List<RegimenDrug> regimenDrugs) {
        this.name = name;
        this.regimenDrugs = regimenDrugs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RegimenDrug> getRegimenDrugs() {
        return regimenDrugs;
    }

    public void setRegimenDrugs(List<RegimenDrug> regimenDrugs) {
        this.regimenDrugs = regimenDrugs;
    }
}
