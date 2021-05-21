package org.openmrs.module.cfl.web.service;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.springframework.beans.PropertyValues;

import java.util.List;

/**
 * The CFLRegistrationUiService Class.
 * <p>
 * The service which provides CfL-UI specific functionality in context of patient and caregiver registration.
 * The main reason for this component to exist is adapt data from CfL-UI to the registration process implemented in the
 * registrationcore module. This component works on UI layer.
 * </p>
 * <p>
 * The implementation of this service must be a Spring-managed bean.
 * </p>
 */
public interface CFLRegistrationUiService {
    Patient createOrUpdatePatient(PropertyValues registrationProperties);

    Person createOrUpdatePerson(PropertyValues registrationProperties);

    List<Relationship> parseRelationships(PropertyValues registrationProperties);

    void updateRelationships(Person person, List<Relationship> newRelationships);
}
