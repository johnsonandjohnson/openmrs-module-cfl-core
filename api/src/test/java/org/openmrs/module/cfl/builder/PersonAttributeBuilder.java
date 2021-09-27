package org.openmrs.module.cfl.builder;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;

public class PersonAttributeBuilder extends AbstractBuilder<PersonAttribute> {

    private Integer id;

    private Person person;

    private String value;

    private PersonAttributeType personAttributeType;

    public PersonAttributeBuilder() {
        super();
        id = getInstanceNumber();
        person = new PersonBuilder().build();
        value = "48100200300";
        personAttributeType = new PersonAttributeTypeBuilder().build();
    }

    @Override
    public PersonAttribute build() {
        PersonAttribute personAttribute = new PersonAttribute();
        personAttribute.setId(id);
        personAttribute.setPerson(person);
        personAttribute.setValue(value);
        personAttribute.setAttributeType(personAttributeType);
        return personAttribute;
    }

    @Override
    public PersonAttribute buildAsNew() {
        return withId(null).build();
    }

    public PersonAttributeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public PersonAttributeBuilder withPerson(Person person) {
        this.person = person;
        return this;
    }

    public PersonAttributeBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public PersonAttributeBuilder withPersonAttributeType(PersonAttributeType personAttributeType) {
        this.personAttributeType = personAttributeType;
        return this;
    }
}
