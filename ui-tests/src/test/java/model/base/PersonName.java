package model.base;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PersonName implements Serializable {

    private static final long serialVersionUID = 7758334433533168677L;

    private String givenName;
    private String familyName;

    public String getGivenName() {
        return givenName;
    }

    public PersonName setGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public String getFamilyName() {
        return familyName;
    }

    public PersonName setFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
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
