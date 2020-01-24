package model.base;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Relationship implements Serializable {

    private static final long serialVersionUID = -514727790870246296L;

    private String personA;
    private String personB;
    private String relationshipType;

    public Relationship() { }

    public Relationship(String personA, String personB, String relationshipType) {
        this.personA = personA;
        this.personB = personB;
        this.relationshipType = relationshipType;
    }

    public String getPersonA() {
        return personA;
    }

    public void setPersonA(String personA) {
        this.personA = personA;
    }

    public String getPersonB() {
        return personB;
    }

    public void setPersonB(String personB) {
        this.personB = personB;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
