package org.openmrs.module.cfl.api.contract;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Vaccination {
    private String name;
    private int numberOfDose;
    private List<VisitInformation> visits = new ArrayList<VisitInformation>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfDose() {
        return numberOfDose;
    }

    public void setNumberOfDose(int numberOfDose) {
        this.numberOfDose = numberOfDose;
    }

    public List<VisitInformation> getVisits() {
        return visits;
    }

    public void setVisits(List<VisitInformation> visits) {
        this.visits = visits;
    }

    public VisitInformation findByVisitName(String visitName) {
        VisitInformation visitInformation = null;
        for (VisitInformation vi : getVisits()) {
            if (StringUtils.equals(vi.getNameOfDose(), visitName)) {
                visitInformation = vi;
                break;
            }
        }
        return visitInformation;
    }
}
