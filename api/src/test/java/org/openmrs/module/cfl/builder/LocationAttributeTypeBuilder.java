package org.openmrs.module.cfl.builder;

import org.openmrs.LocationAttributeType;

public class LocationAttributeTypeBuilder extends AbstractBuilder<LocationAttributeType> {

    private Integer id;

    private String name;

    public LocationAttributeTypeBuilder() {
        super();
        id = getInstanceNumber();
        name = "Country decoded";
    }

    @Override
    public LocationAttributeType build() {
        LocationAttributeType locationAttributeType = new LocationAttributeType();
        locationAttributeType.setId(id);
        locationAttributeType.setName(name);
        return locationAttributeType;
    }

    @Override
    public LocationAttributeType buildAsNew() {
        return withId(id).withName(name).build();
    }

    public LocationAttributeTypeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public LocationAttributeTypeBuilder withName(String name) {
        this.name = name;
        return this;
    }
}
