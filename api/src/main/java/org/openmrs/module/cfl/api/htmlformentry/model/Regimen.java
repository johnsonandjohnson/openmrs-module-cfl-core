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
