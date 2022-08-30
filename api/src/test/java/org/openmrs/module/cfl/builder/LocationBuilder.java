package org.openmrs.module.cfl.builder;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;

public class LocationBuilder extends AbstractBuilder<Location> {

    private Integer id;

    private String name;

    private LocationAttribute locationAttribute;

    private String country;

    public LocationBuilder() {
        super();
        id = getInstanceNumber();
        name = "Test name";
        locationAttribute = new LocationAttributeBuilder().build();
        country = "testCountryName";
    }

    @Override
    public Location build() {
        Location location = new Location();
        location.setId(id);
        location.setName(name);
        location.setAttribute(locationAttribute);
        location.setCountry(country);
        return location;
    }

    @Override
    public Location buildAsNew() {
        return withId(id).withName(name).withAttribute(locationAttribute).build();
    }

    public LocationBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public LocationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public LocationBuilder withAttribute(LocationAttribute locationAttribute) {
        this.locationAttribute = locationAttribute;
        return this;
    }

    public LocationBuilder withCountry(String country) {
        this.country = country;
        return this;
    }
}
