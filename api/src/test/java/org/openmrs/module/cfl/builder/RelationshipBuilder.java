package org.openmrs.module.cfl.builder;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.module.cfl.Constant;

import java.util.UUID;

public class RelationshipBuilder extends AbstractBuilder<Relationship> {

    private Person personA;
    private Person personB;
    private RelationshipType relationshipType;
    private Integer id;
    private String uuid;

    public RelationshipBuilder() {
        this.personA = new PersonBuilder().buildAsNew();
        this.personB = new PersonBuilder().buildAsNew();
        this.relationshipType = new RelationshipTypeBuilder().buildAsNew();
        this.id = getInstanceNumber();
        this.uuid = Constant.RELATIONSHIP_UUID;
    }

    @Override
    public Relationship build() {
        Relationship relationship = new Relationship();
        relationship.setPersonA(this.personA);
        relationship.setPersonB(this.personB);
        relationship.setRelationshipType(this.relationshipType);
        relationship.setRelationshipId(this.id);
        relationship.setUuid(this.uuid);
        return relationship;
    }

    @Override
    public Relationship buildAsNew() {
        return this.withId(null)
            .withUuid(UUID.randomUUID().toString())
            .build();
    }

    public RelationshipBuilder withPersonA(Person personA) {
        this.personA = personA;
        return this;
    }

    public RelationshipBuilder withPersonB(Person personB) {
        this.personB = personB;
        return this;
    }

    public RelationshipBuilder withRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
        return this;
    }

    public RelationshipBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public RelationshipBuilder withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }
}
