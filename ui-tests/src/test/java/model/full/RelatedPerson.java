package model.full;

public class RelatedPerson {

    private String relationshipType;
    private FullPatient personB;

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public FullPatient getPersonB() {
        return personB;
    }

    public void setPersonB(FullPatient personB) {
        this.personB = personB;
    }
}
