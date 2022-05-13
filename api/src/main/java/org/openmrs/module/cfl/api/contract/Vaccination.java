/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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

    public List<VisitInformation> findFutureVisits(String visitType, int numberOfVisits) {
        List<VisitInformation> futureVisitInformation = new ArrayList<VisitInformation>();
        for (int i = 0; i < visits.size(); i++) {
            if (StringUtils.equalsIgnoreCase(visits.get(i).getNameOfDose(), visitType)
                    && (numberOfVisits == 1 || visits.get(i).getDoseNumber() == numberOfVisits)) {
                    for (int j = 1; j <= visits.get(i).getNumberOfFutureVisit(); j++) {
                        futureVisitInformation.add(visits.get(i + j));
                    }
                    break;
            }
        }
        return futureVisitInformation;
    }

}
