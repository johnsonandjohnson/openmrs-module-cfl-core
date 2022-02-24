package org.openmrs.module.cfl.api.service;

import org.openmrs.Concept;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/** Provides methods related to countries functionalities */
public interface CountryService {

  /**
   * Creates required resources for a new country or country members i.e. ConceptName and Concept.
   *
   * @param name name of country or country member
   * @param shortName short name of country, nullable
   * @return country concept
   */
  Concept buildAndSaveCountryResources(String name, String shortName);

  /**
   * Creates country resources or adds existing concept to list of duplicated countries
   *
   * @param countriesResourcesMap map of countries resources
   * @return list of duplicated countries
   */
  List<String> processAndReturnAlreadyExistingCountries(Map<String, String> countriesResourcesMap);

  /**
   * Retrieves map of countries resources from file
   *
   * @param inputStream input stream of bytes
   * @return map of countries resources
   * @throws IOException if something issue occurs during reading bytes from stream
   */
  Map<String, String> getCountriesListFromFile(InputStream inputStream) throws IOException;

  /**
   * Creates required resources for cluster members
   *
   * @param clusterMembersList list of cluster members for which resources should be created
   * @param countryConcept concept for which cluster members resources are created
   */
  void createClusterMembersResources(String clusterMembersList, Concept countryConcept);

  /**
   * Gets or creates concept if it does not exist
   *
   * @param name concept name
   * @return existing or new concept
   */
  Concept getOrCreateCountryConcept(String name);
}
