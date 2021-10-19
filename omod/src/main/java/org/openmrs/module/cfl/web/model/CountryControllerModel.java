package org.openmrs.module.cfl.web.model;

import java.util.Map;

/**
 * The AddCountryControllerModel class
 */
public class CountryControllerModel {

    private String name;

    private Map<Integer, String> conceptMap;

    public CountryControllerModel() {
    }

    public CountryControllerModel(Map<Integer, String> conceptMap) {
        this.conceptMap = conceptMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, String> getConceptMap() {
        return conceptMap;
    }

    public void setConceptMap(Map<Integer, String> conceptMap) {
        this.conceptMap = conceptMap;
    }
}
