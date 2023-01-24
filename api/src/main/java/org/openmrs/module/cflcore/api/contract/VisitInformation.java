/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.contract;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Objects;

public class VisitInformation {
    private int doseNumber;
    private String nameOfDose;
    private String visitTime;
    private int midPointWindow;
    private int lowWindow;
    private int upWindow;
    private int numberOfFutureVisit;

    public int getDoseNumber() {
        return doseNumber;
    }

    public void setDoseNumber(int doseNumber) {
        this.doseNumber = doseNumber;
    }

    public String getNameOfDose() {
        return nameOfDose;
    }

    public void setNameOfDose(String nameOfDose) {
        this.nameOfDose = nameOfDose;
    }

    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public int getMidPointWindow() {
        return midPointWindow;
    }

    public void setMidPointWindow(int midPointWindow) {
        this.midPointWindow = midPointWindow;
    }

    public int getLowWindow() {
        return lowWindow;
    }

    public void setLowWindow(int lowWindow) {
        this.lowWindow = lowWindow;
    }

    public int getUpWindow() {
        return upWindow;
    }

    public void setUpWindow(int upWindow) {
        this.upWindow = upWindow;
    }

    public int getNumberOfFutureVisit() {
        return numberOfFutureVisit;
    }

    public void setNumberOfFutureVisit(int numberOfFutureVisit) {
        this.numberOfFutureVisit = numberOfFutureVisit;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doseNumber, nameOfDose, visitTime, midPointWindow, lowWindow, upWindow, numberOfFutureVisit);
    }
}
