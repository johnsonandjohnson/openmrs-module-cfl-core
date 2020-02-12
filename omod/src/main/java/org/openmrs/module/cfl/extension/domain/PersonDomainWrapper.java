package org.openmrs.module.cfl.extension.domain;

import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.module.emrapi.domainwrapper.DomainWrapper;

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
}
