package model.full;

import model.base.Patient;
import model.base.PatientIdentifier;
import model.base.Person;

import java.util.List;

public class FullPatient {

    private Person person;
    private List<PatientIdentifier> identifiers;
    private List<RelatedPerson> related;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<PatientIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<PatientIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public Patient getBaseRepresentation() {
        return new Patient(person.getUuid(), identifiers);
    }

    public List<RelatedPerson> getRelated() {
        return related;
    }

    public void setRelated(List<RelatedPerson> related) {
        this.related = related;
    }
}
