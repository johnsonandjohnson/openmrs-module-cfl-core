package org.openmrs.module.cfl.builder;

import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;

public class LocationAttributeBuilder extends AbstractBuilder<LocationAttribute> {

    private Integer id;

    private String value;

    private LocationAttributeType locationAttributeType;

    public LocationAttributeBuilder() {
        super();
        id = getInstanceNumber();
        value = "Poland";
        locationAttributeType = new LocationAttributeTypeBuilder().build();
    }

    @Override
    public LocationAttribute build() {
        LocationAttribute locationAttribute = new LocationAttribute();
        locationAttribute.setId(id);
        locationAttribute.setValueReferenceInternal(value);
        locationAttribute.setAttributeType(locationAttributeType);
        return locationAttribute;
    }

    @Override
    public LocationAttribute buildAsNew() {
        return withId(id).withValue(value).withAttributeType(locationAttributeType).build();
    }

    public LocationAttributeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public LocationAttributeBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public LocationAttributeBuilder withAttributeType(LocationAttributeType locationAttributeType) {
        this.locationAttributeType = locationAttributeType;
        return this;
    }
}
