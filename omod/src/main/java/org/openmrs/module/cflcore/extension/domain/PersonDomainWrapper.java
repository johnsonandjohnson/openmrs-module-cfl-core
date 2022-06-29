/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.extension.domain;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapper;

import java.util.Date;
import java.util.Set;

/**
 This class copies required parts of the logic org.openmrs.module.emrapi.patient.PatientDomainWrapper
 but adjusted to support Person object instead of Patient only
 */
public class PersonDomainWrapper implements DomainWrapper {

    private Person person;

    public PersonName getPersonName() {
        Set<PersonName> names = person.getNames();
        if (names != null && names.size() > 0) {
            for (PersonName name : names) {
                if (name.isPreferred()) {
                    return name;
                }
            }
            for (PersonName name : names) {
                return name;
            }

        }
        return null;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Integer getAgeInMonths() {
        if (this.person.getBirthdate() == null) {
            return null;
        } else {
            Date endDate = this.person.isDead() ? this.person.getDeathDate() : new Date();
            return Months.monthsBetween(new DateTime(this.person.getBirthdate()), new DateTime(endDate)).getMonths();
        }
    }

    public Integer getAgeInDays() {
        if (this.person.getBirthdate() == null) {
            return null;
        } else {
            Date endDate = this.person.isDead() ? this.person.getDeathDate() : new Date();
            return Days.daysBetween(new DateTime(this.person.getBirthdate()), new DateTime(endDate)).getDays();
        }
    }

}
