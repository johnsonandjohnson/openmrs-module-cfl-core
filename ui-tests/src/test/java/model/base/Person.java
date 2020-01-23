package model.base;

import java.util.List;

public class Person {

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
}
