package model.base;

public class Relationship {

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
}
