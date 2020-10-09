package org.openmrs.module.cfl.api.contract;

import java.util.List;

public class Randomization {
    private List<Vaccination> vaccinations;

    public List<Vaccination> getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(List<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }

    public Vaccination findByVaccinationName(String name) {
        return null;
    }
}
