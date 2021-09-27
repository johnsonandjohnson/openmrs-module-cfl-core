package org.openmrs.module.cfl.builder;

import org.openmrs.PersonAttributeType;

public class PersonAttributeTypeBuilder extends AbstractBuilder<PersonAttributeType> {

    private Integer id;

    private String name;

    private String description;

    private String format;

    private Boolean searchable;

    public PersonAttributeTypeBuilder() {
        super();
        id = getInstanceNumber();
        name = "Telephone Number";
        description = "The telephone number for the person";
        format = "java.lang.String";
        searchable = false;
    }

    @Override
    public PersonAttributeType build() {
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setId(id);
        personAttributeType.setName(name);
        personAttributeType.setDescription(description);
        personAttributeType.setFormat(format);
        personAttributeType.setSearchable(searchable);
        return personAttributeType;
    }

    @Override
    public PersonAttributeType buildAsNew() {
        return withId(null).build();
    }

    public PersonAttributeTypeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public PersonAttributeTypeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonAttributeTypeBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public PersonAttributeTypeBuilder withFormat(String format) {
        this.format = format;
        return this;
    }

    public PersonAttributeTypeBuilder withSearchable(Boolean searchable) {
        this.searchable = searchable;
        return this;
    }
}
