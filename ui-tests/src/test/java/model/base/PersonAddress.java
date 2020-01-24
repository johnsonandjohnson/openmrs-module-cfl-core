package model.base;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PersonAddress implements Serializable {

    private static final long serialVersionUID = -9118104375112695855L;

    private String address1;

    public String getAddress1() {
        return address1;
    }

    public PersonAddress setAddress1(String address1) {
        this.address1 = address1;
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
