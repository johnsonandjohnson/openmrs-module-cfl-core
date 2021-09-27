package org.openmrs.module.cfl.builder;

import org.openmrs.PersonName;

public class PersonNameBuilder extends AbstractBuilder<PersonName> {

    private Integer id;

    private String givenName;

    private String familyName;

    public PersonNameBuilder() {
        super();
        id = getInstanceNumber();
        givenName = "John";
        familyName = "Smith";
    }

    @Override
    public PersonName build() {
        PersonName personName = new PersonName();
        personName.setId(id);
        personName.setGivenName(givenName);
        personName.setFamilyName(familyName);
        return personName;
    }

    @Override
    public PersonName buildAsNew() {
        return withId(null).build();
    }

    public PersonNameBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public PersonNameBuilder withGiven(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public PersonNameBuilder withFamily(String familyName) {
        this.familyName = familyName;
        return this;
    }
}
