package model.base;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class Person implements Serializable {

    private static final long serialVersionUID = 2856356152347194522L;

    private String uuid;
    private List<PersonName> names;
    private String gender;
    private int age;
    private List<PersonAddress> addresses;

    public Person() { }

    public String getUuid() {
        return uuid;
    }

    public List<PersonName> getNames() {
        return names;
    }

    public Person setNames(List<PersonName> names) {
        this.names = names;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public Person setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public int getAge() {
        return age;
    }

    public Person setAge(int age) {
        this.age = age;
        return this;
    }

    public List<PersonAddress> getAddresses() {
        return addresses;
    }

    public Person setAddresses(List<PersonAddress> addresses) {
        this.addresses = addresses;
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
