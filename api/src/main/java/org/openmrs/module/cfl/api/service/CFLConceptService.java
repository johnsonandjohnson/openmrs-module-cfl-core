package org.openmrs.module.cfl.api.service;

import org.openmrs.Concept;

import java.util.List;

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

    /**
     * Retrieves concepts (members) from concept that is a set
     *
     * @param concept parent concept which has concept members
     * @return list of concepts
     */
    List<Concept> getConceptMembersByConcept(Concept concept);
}
