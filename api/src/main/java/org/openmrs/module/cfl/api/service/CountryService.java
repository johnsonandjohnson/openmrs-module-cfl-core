package org.openmrs.module.cfl.api.service;

import org.openmrs.Concept;

import java.util.List;

/**
 * Provides methods related to countries functionalities
 */
public interface CountryService {

    /**
     * Creates required resources for new country i.e. ConceptName and Concept.
     *
     * @param countryName name of country
     * @return country concept
     */
    Concept buildAndSaveCountryResources(String countryName);

    /**
     * Creates country resources or adds existing concept to list of duplicated countries
     *
     * @param countriesList list of countries names
     * @return list of duplicated countries
     */
    List<String> processCountriesAndReturnDuplicates(List<String> countriesList);
}
