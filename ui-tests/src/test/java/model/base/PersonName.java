package model.base;

public class PersonName {

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
}
