package org.openmrs.module.cfl.api.helper;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.contract.CFLPerson;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
        person.setDateChanged(new Date());
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setUuid(UUID.randomUUID().toString());
        personAttributeType.setName("Vaccination program");
        PersonAttribute attribute = new PersonAttribute();
        attribute.setValue("Covid 1D vaccine");
        attribute.setPerson(person);
        attribute.setAttributeType(personAttributeType);
        person.addAttribute(attribute);
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

    public static void updatePersonWithRegimenAttribute(Person person) {
        Set<PersonAttribute> personAttributes = new HashSet<PersonAttribute>();
        PersonAttribute personAttribute = new PersonAttribute();
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setName(CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME);
        personAttribute.setAttributeType(personAttributeType);
        personAttribute.setValue(Constant.COVID_VACCINATION_PROGRAM);
        personAttributes.add(personAttribute);
        person.setAttributes(personAttributes);
    }

    public static void updatePersonWithOldAndNewRegimenAttribute(Person person) {
        Set<PersonAttribute> personAttributes = new HashSet<PersonAttribute>();
        PersonAttribute personAttribute = new PersonAttribute();
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setName(CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME);
        personAttribute.setAttributeType(personAttributeType);
        personAttribute.setValue(Constant.COVID_VACCINATION_PROGRAM);
        personAttributes.add(personAttribute);
        personAttribute = new PersonAttribute();
        personAttribute.setAttributeType(personAttributeType);
        personAttribute.setValue("OLD REGIMEN");
        personAttribute.setVoided(true);
        personAttribute.setDateVoided(new Date());
        personAttributes.add(personAttribute);
        person.setAttributes(personAttributes);
    }

    public static PersonAttribute createPersonAttribute(String attributeType, String value) {
        PersonAttribute personAttribute = new PersonAttribute();
        personAttribute.setAttributeType(createPersonAttributeType(attributeType));
        personAttribute.setValue(value);
        return personAttribute;
    }

    public static PersonAttributeType createPersonAttributeType(String attributeType) {
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setName(attributeType);
        return personAttributeType;
    }

    private PersonHelper() {
    }
}
