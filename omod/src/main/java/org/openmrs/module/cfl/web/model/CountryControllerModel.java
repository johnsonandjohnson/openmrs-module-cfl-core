package org.openmrs.module.cfl.web.model;

import java.util.Map;

/** The AddCountryControllerModel class */
public class CountryControllerModel {

  private String name;

  private Map<Integer, String> conceptMap;

  private String clusterMembers;

  public CountryControllerModel() {}

  public CountryControllerModel(Map<Integer, String> conceptMap) {
    this.conceptMap = conceptMap;
  }

  public CountryControllerModel(String name, String clusterMembers) {
    this.name = name;
    this.clusterMembers = clusterMembers;
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

  public String getClusterMembers() {
    return clusterMembers;
  }

  public void setClusterMembers(String clusterMembers) {
    this.clusterMembers = clusterMembers;
  }
}
