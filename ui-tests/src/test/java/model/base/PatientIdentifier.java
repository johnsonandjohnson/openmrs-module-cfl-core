package model.base;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PatientIdentifier {

    private String identifier;
    private String identifierType;
    private boolean preferred;
    private String location;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public boolean getPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PatientIdentifier that = (PatientIdentifier) o;

        return new EqualsBuilder()
                .append(getPreferred(), that.getPreferred())
                .append(getIdentifier(), that.getIdentifier())
                .append(getIdentifierType(), that.getIdentifierType())
                .append(getLocation(), that.getLocation())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getIdentifier())
                .append(getIdentifierType())
                .append(getPreferred())
                .append(getLocation())
                .toHashCode();
    }
}
