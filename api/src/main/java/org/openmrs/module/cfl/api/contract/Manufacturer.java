package org.openmrs.module.cfl.api.contract;

public class Manufacturer {

    private String name;

    private String barcodeRegex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcodeRegex() {
        return barcodeRegex;
    }

    public void setBarcodeRegex(String barcodeRegex) {
        this.barcodeRegex = barcodeRegex;
    }
}
