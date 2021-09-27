package org.openmrs.module.cfl.api.contract;

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
