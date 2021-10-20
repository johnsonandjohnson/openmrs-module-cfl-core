package org.openmrs.module.cfl.api.service;

/**
 * Provides custom methods related to concept functionalities
 */
public interface CFLConceptService {

    /**
     * Retrieves concept description from concept in given language
     *
     * @param conceptName name of concept
     * @param language language in which description content is retrieved
     * @return description content
     */
    String getMessageByConceptNameAndLanguage(String conceptName, String language);
}
