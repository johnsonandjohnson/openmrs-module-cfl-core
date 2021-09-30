package org.openmrs.module.cfl.api.contract;

public class RandomizationRegimen {

    private Regimen regimens;

    public RandomizationRegimen(Regimen regimens) {
        this.regimens = regimens;
    }

    public Regimen getRegimens() {
        return regimens;
    }

    public void setRegimens(Regimen regimens) {
        this.regimens = regimens;
    }
}
