package org.openmrs.module.cfl.api.service;

import org.openmrs.Concept;

import java.io.IOException;
import java.io.InputStream;
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
    List<String> processAndReturnAlreadyExistingCountries(List<String> countriesList);

    /**
     * Retrieves list of countries from file
     *
     * @param inputStream input stream of bytes
     * @return list of countries
     * @throws IOException if something issue occurs during reading bytes from stream
     */
    List<String> getCountriesListFromFile(InputStream inputStream) throws IOException;
}
