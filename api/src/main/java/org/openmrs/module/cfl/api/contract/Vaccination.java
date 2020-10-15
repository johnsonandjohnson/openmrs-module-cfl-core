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

    public List<VisitInformation> findByVisitType(String visitType) {
        List<VisitInformation> visitInformation = new ArrayList<VisitInformation>();
        for (VisitInformation vi : getVisits()) {
            if (StringUtils.equals(vi.getNameOfDose(), visitType)) {
                visitInformation.add(vi);
            }
        }
        return visitInformation;
    }

    public List<VisitInformation> findFutureVisits(String visitType, Integer numberOfVisits) {
        List<VisitInformation> futureVisitInformation = new ArrayList<VisitInformation>();
        for (int i = 0; i < visits.size(); i++) {
            if (StringUtils.equalsIgnoreCase(visits.get(i).getNameOfDose(), visitType)) {
                for (int j = 1; j <= visits.get(i).getNumberOfFutureVisit(); j++) {
                    futureVisitInformation.add(visits.get(i + j));
                }
            }
        }
        return futureVisitInformation;
    }
}
