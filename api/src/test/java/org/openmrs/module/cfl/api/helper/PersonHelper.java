package org.openmrs.module.cfl.api.helper;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.contract.CFLPerson;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PersonHelper {

    public static final String JOHN_GIVEN_NAME = "John";

    public static final String JOHN_FAMILY_NAME = "Doe";

    public static final String ADAM_GIVEN_NAME = "Adam";

    public static final String ADAM_FAMILY_NAME = "Smith";

    public static boolean containsPerson(List<CFLPerson> cflPeople, String fullName) {
        boolean contain = false;
        for (CFLPerson cflPerson : cflPeople) {
            if (StringUtils.equals(fullName, cflPerson.getPerson().getPersonName().toString())) {
                contain = true;
                break;
            }
        }

        return contain;
    }

    public static Person createPerson() {
        Person person = new Person();
        person.setUuid(Constant.PERSON_UUID);
        return person;
    }

    public static void updatePersonWithLocationAttribute(Person person) {
        Set<PersonAttribute> personAttributes = new HashSet<PersonAttribute>();
        PersonAttribute personAttribute = new PersonAttribute();
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setName(CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE);
        personAttribute.setAttributeType(personAttributeType);
        personAttribute.setValue(Constant.LOCATION_UUID);
        personAttributes.add(personAttribute);
        person.setAttributes(personAttributes);
    }

    private PersonHelper() {
    }
}
