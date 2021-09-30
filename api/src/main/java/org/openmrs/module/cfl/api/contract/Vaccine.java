package org.openmrs.module.cfl.api.contract;

import java.util.List;

public class Vaccine {

    private String name;

    private List<String > manufacturers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(List<String> manufacturers) {
        this.manufacturers = manufacturers;
    }
}
