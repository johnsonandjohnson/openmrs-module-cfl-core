package org.openmrs.module.cfl.api.contract;

public class AuthSteps {

    private String type;

    private boolean mandatory;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
