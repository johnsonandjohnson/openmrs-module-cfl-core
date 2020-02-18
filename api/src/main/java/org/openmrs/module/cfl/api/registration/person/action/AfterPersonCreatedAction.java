package org.openmrs.module.cfl.api.registration.person.action;

import org.openmrs.Person;

import java.util.Map;

/**
 * An action that can be configured to run after creating a person, for a particular instance of the registration app
 */
public interface AfterPersonCreatedAction {

    /**
     * Method which will be called after creating a person.
     *
     * @param created - the person who is being created
     * @param submittedParameters - parameters, typically from an HttpServletRequest
     */
    void afterCreated(Person created, Map<String, String[]> submittedParameters);
}
