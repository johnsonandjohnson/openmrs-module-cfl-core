package org.openmrs.module.cfl.builder;

import org.openmrs.Location;
import org.openmrs.PatientIdentifier;

import java.util.HashSet;
import java.util.Set;

public class PatientIdentifierBuilder extends AbstractBuilder<PatientIdentifier> {

    private Integer id;

    private Location location;

    private String identifier;

    public PatientIdentifierBuilder() {
        super();
        id = getInstanceNumber();

    }

    @Override
    public PatientIdentifier buildAsNew() {
        return withId(id).withLocation(location).withIdentifier(identifier).build();
    }

    @Override
    public PatientIdentifier build() {
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setId(id);
        patientIdentifier.setLocation(location);
        patientIdentifier.setIdentifier(identifier);
        return patientIdentifier;
    }

    public Set<PatientIdentifier> buildPatientIdentifierSet() {
        Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
        patientIdentifiers.add(build());
        return patientIdentifiers;
    }

    public PatientIdentifierBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public PatientIdentifierBuilder withLocation(Location location) {
        this.location = location;
        return this;
    }

    public PatientIdentifierBuilder withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

}
