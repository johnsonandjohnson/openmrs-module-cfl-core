/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.PersonService;
import org.openmrs.module.cfl.web.service.CFLRegistrationUiService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.HttpURLConnection;
import java.util.List;

@Api(value = "Caregiver registration", tags = {"REST API for Caregiver registration"})
@Controller("cfl.caregiverRegistrationController")
public class CaregiverRegistrationController extends BaseCflModuleRestController {

    @Autowired
    private PersonService personService;

    @Autowired
    private CFLRegistrationUiService cflRegistrationUiService;

    @ApiOperation(value = "Caregiver registration", notes = "Caregiver registration")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful registration of Caregiver"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to register Caregiver"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in registration of Caregiver")})
    @RequestMapping(value = "/caregiverRegistration", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public ResponseEntity<String> registerCaregiver(
            @ApiParam(name = "registrationRequestBody", value = "Request body")
            final @RequestBody SimpleObject registrationRequestBody) {
        final PropertyValues registrationProperties = new MutablePropertyValues(registrationRequestBody);

        final Person person = cflRegistrationUiService.createOrUpdatePerson(registrationProperties);
        final Person savedPerson = personService.savePerson(person);

        final List<Relationship> personRelationships = cflRegistrationUiService.parseRelationships(registrationProperties);
        cflRegistrationUiService.updateRelationships(person, personRelationships);

        return new ResponseEntity<>(savedPerson.getUuid(), HttpStatus.OK);
    }
}
