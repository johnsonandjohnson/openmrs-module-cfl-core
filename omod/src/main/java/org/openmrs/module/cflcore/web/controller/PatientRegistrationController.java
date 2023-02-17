/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.openmrs.Patient;
import org.openmrs.Relationship;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.module.cflcore.web.service.CFLRegistrationUiService;
import org.openmrs.module.registrationcore.RegistrationData;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
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

import javax.servlet.ServletRequest;
import java.net.HttpURLConnection;
import java.util.List;

@Api(
    value = "Patient registration",
    tags = {"REST API for Patient registration"})
@Controller("cfl.patientRegistrationController")
public class PatientRegistrationController extends BaseCflModuleRestController {

  @Autowired private RegistrationCoreService registrationCoreService;

  @Autowired private PatientService patientService;

  @Autowired private CFLRegistrationUiService cflRegistrationUiService;

  @Autowired private UiUtils uiUtils;

  @ApiOperation(value = "Patient registration", notes = "Patient registration")
  @ApiResponses(
      value = {
          @ApiResponse(
              code = HttpURLConnection.HTTP_OK,
              message = "On successful registration of Patient"),
          @ApiResponse(
              code = HttpURLConnection.HTTP_INTERNAL_ERROR,
              message = "Failure to register Patient"),
          @ApiResponse(
              code = HttpURLConnection.HTTP_BAD_REQUEST,
              message = "Error in registration of Patient")
      })
  @RequestMapping(value = "/patientRegistration", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> registerPatient(
      @ApiParam(name = "registrationRequest", value = "Request")
      final ServletRequest registrationRequest,
      @ApiParam(name = "registrationRequestBody", value = "Request body")
      final @RequestBody SimpleObject registrationRequestBody) {
    final PropertyValues registrationProperties =
        new MutablePropertyValues(registrationRequestBody);

    final RegistrationData registrationData =
        cflRegistrationUiService.preparePatientRegistration(registrationProperties);
    final Patient registeredPatient = registrationCoreService.registerPatient(registrationData);

    flashInfoMessage(registrationRequest, registeredPatient);

    return new ResponseEntity<>(registeredPatient.getUuid(), HttpStatus.OK);
  }

  private void flashInfoMessage(final ServletRequest registrationRequest, final Patient patient) {
    registrationRequest.setAttribute(
        "emr.infoMessage",
        uiUtils.message(
            "cfl.createdPatientMessage", uiUtils.encodeHtml(patient.getPersonName().toString())));
    registrationRequest.setAttribute("emr.toastMessage", "true");
  }

  @ApiOperation(value = "Update Patient", notes = "Update Patient")
  @ApiResponses(
      value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful updating Patient"),
          @ApiResponse(
              code = HttpURLConnection.HTTP_INTERNAL_ERROR,
              message = "Failure to update Patient"),
          @ApiResponse(
              code = HttpURLConnection.HTTP_BAD_REQUEST,
              message = "Error in updating Patient")
      })
  @RequestMapping(value = "/patientRegistration", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<String> updatePatient(
      @ApiParam(name = "registrationRequestBody", value = "Request body")
      final @RequestBody SimpleObject registrationRequestBody) {
    final Object patientUuidRaw = registrationRequestBody.get("uuid");

    if (!(patientUuidRaw instanceof String)) {
      throw new APIException("Invalid Patient UUID, expected String but got: " + patientUuidRaw);
    }

    final PropertyValues registrationProperties =
        new MutablePropertyValues(registrationRequestBody);
    final Patient patientToUpdate =
        cflRegistrationUiService.createOrUpdatePatient(registrationProperties);
    patientService.savePatient(patientToUpdate);

    final List<Relationship> patientRelationships =
        cflRegistrationUiService.parseRelationships(registrationProperties);
    cflRegistrationUiService.updateRelationships(patientToUpdate, patientRelationships);

    return new ResponseEntity<>(patientToUpdate.getUuid(), HttpStatus.OK);
  }
}
