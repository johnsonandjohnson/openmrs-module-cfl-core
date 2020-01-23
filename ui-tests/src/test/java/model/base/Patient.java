package model.base;

import java.util.List;

public class Patient {

    private String person;
    private List<PatientIdentifier> identifiers;

    public Patient() { }

    public Patient(String person, List<PatientIdentifier> identifiers) {
        this.person = person;
        this.identifiers = identifiers;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public List<PatientIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<PatientIdentifier> identifiers) {
        this.identifiers = identifiers;
    }
}
