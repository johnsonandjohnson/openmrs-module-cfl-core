/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.web.service;

import java.util.List;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.module.registrationcore.RegistrationData;
import org.springframework.beans.PropertyValues;

/**
 * The CFLRegistrationUiService Class.
 * <p>
 * The service which provides CfL-UI specific functionality in context of patient and caregiver
 * registration. The main reason for this component to exist is adapt data from CfL-UI to the
 * registration process implemented in the registrationcore module. This component works on UI
 * layer.
 * </p>
 * <p>
 * The implementation of this service must be a Spring-managed bean.
 * </p>
 */
public interface CFLRegistrationUiService {

  RegistrationData preparePatientRegistration(PropertyValues registrationProperties);

  Patient createOrUpdatePatient(PropertyValues registrationProperties);

  Person createOrUpdatePerson(PropertyValues registrationProperties);

  List<Relationship> parseRelationships(PropertyValues registrationProperties);

  void updateRelationships(Person person, List<Relationship> newRelationships);
}
