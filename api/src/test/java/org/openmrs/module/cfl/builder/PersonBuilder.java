package org.openmrs.module.cfl.builder;

import org.openmrs.Person;
import org.openmrs.PersonName;

import java.util.UUID;

public class PersonBuilder extends AbstractBuilder<Person> {

    private Integer id;

    private PersonName name;

    private String gender;

    private String uuid;

    public PersonBuilder() {
        super();
        id = getInstanceNumber();
        name = new PersonNameBuilder().build();
        gender = "M";
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public Person build() {
        Person person = new Person();
        person.setId(id);
        person.addName(name);
        person.setGender(gender);
        person.setUuid(this.uuid);
        return person;
    }

    @Override
    public Person buildAsNew() {
        return withId(null)
                .withName(new PersonNameBuilder().buildAsNew())
                .build();
    }

    public PersonBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public PersonBuilder withName(PersonName name) {
        this.name = name;
        return this;
    }

    public PersonBuilder withGender(String gender) {
        this.gender = gender;
        return this;
    }

    public PersonBuilder withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }
}
