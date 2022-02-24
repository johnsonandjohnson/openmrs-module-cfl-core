package org.openmrs.module.cfl.web.model;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;

import java.util.Map;

/** The AddCountryControllerModel class */
public class CountryControllerModel {

  private String name;

  private String countryCode;

  private Map<Integer, String> conceptMap;

  private String clusterMembers;

  public CountryControllerModel() {}

  public CountryControllerModel(Map<Integer, String> conceptMap) {
    this.conceptMap = conceptMap;
  }

  public CountryControllerModel(Concept countryConcept, String clusterMembers) {
    ConceptName countryConceptFullName = countryConcept.getName();
    this.name = countryConceptFullName != null ? countryConceptFullName.getName() : null;
    ConceptName countryConceptShortName = countryConcept.getShortNameInLocale(Context.getLocale());
    this.countryCode = countryConceptShortName != null ? countryConceptShortName.getName() : null;
    this.clusterMembers = clusterMembers;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
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
