package org.openmrs.module.cfl.api.registration.person.action;

import org.openmrs.Person;

import java.util.Map;

/**
 * An action that can be run after creating a person.
 * <p>
 * <ul>
 * <li>For UI using CfL-solution, all beans which implement the `AfterPersonCreatedAction` are called after Caregiver
 * registration.</li>
 * <li><i>(Deprecated)</i> For UI using core registration app, which use `RegisterPatientFragmentController`, the usage of an
 * action must be
 * directly
 * configured in relevant app JSON.</li>
 * </ul>
 * </p>
 */
public interface AfterPersonCreatedAction {

    /**
     * Method which will be called after creating a person.
     *
     * @param created             - the person who is being created
     * @param submittedParameters - parameters, typically from an HttpServletRequest
     */
    void afterCreated(Person created, Map<String, String[]> submittedParameters);
}
