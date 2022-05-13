/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
