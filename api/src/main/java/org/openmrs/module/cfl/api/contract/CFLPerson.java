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

import org.openmrs.Person;
import org.openmrs.PersonAttribute;

public class CFLPerson {
    private static final String TELEPHONE_NUMBER_TYPE = "Telephone Number";
    private Person person;
    private String phone;
    private boolean caregiver;
    private boolean patient;
    private Integer personId;

    public CFLPerson(Person person, boolean caregiver) {
        this.person = person;
        this.personId = person.getPersonId();
        this.caregiver = caregiver;
        this.patient = person.isPatient();
        PersonAttribute personAttribute = person.getAttribute(TELEPHONE_NUMBER_TYPE);
        this.phone = personAttribute != null ? personAttribute.getValue() : null;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isCaregiver() {
        return caregiver;
    }

    public void setCaregiver(boolean caregiver) {
        this.caregiver = caregiver;
    }

    public boolean isPatient() {
        return patient;
    }

    public void setPatient(boolean patient) {
        this.patient = patient;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }
}
