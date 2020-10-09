package org.openmrs.module.cfl.api.contract;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Randomization {
    private List<Vaccination> vaccinations = new ArrayList<Vaccination>();

    public List<Vaccination> getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(List<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }

    public Vaccination findByVaccinationName(String name) {
        Vaccination vaccination = null;
        for (Vaccination vacc : getVaccinations()) {
            if (StringUtils.equals(vacc.getName(), name)) {
                vaccination = vacc;
                break;
            }
        }
        return vaccination;
    }
}
