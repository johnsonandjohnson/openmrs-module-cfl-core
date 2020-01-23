package model.base;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class Patient implements Serializable {

    private static final long serialVersionUID = 7554081006870427221L;

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
