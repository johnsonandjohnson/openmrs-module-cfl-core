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
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletRequest;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.Relationship;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.util.GlobalPropertyUtils;
import org.openmrs.module.cflcore.web.service.CFLRegistrationUiService;
import org.openmrs.module.messages.api.constants.MessagesConstants;
import org.openmrs.module.messages.api.model.PatientTemplate;
import org.openmrs.module.messages.api.service.DefaultPatientTemplateService;
import org.openmrs.module.messages.api.service.TemplateFieldValueService;
import org.openmrs.module.registrationcore.RegistrationData;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(value = "Patient registration", tags = {"REST API for Patient registration"})
@Controller("cfl.patientRegistrationController")
public class PatientRegistrationController extends BaseCflModuleRestController {

  private static final Integer SHOULD_CREATE_PATIENT_TEMPLATES_INDEX = 0;

  private static final Integer DEFAULT_PATIENT_TEMPLATES_CHANNEL_TYPE_INDEX = 1;

  @Autowired
  private RegistrationCoreService registrationCoreService;

  @Autowired
  private PatientService patientService;

  @Autowired
  private CFLRegistrationUiService cflRegistrationUiService;

  @Autowired
  @Qualifier("adminService")
  private AdministrationService administrationService;

  @Autowired
  private LocationService locationService;

  @Autowired
  private UiUtils uiUtils;

  @ApiOperation(value = "Patient registration", notes = "Patient registration")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful registration of Patient"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to register Patient"),
      @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in registration of Patient")})
  @RequestMapping(value = "/patientRegistration", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> registerPatient(
      @ApiParam(name = "registrationRequest", value = "Request") final ServletRequest registrationRequest,
      @ApiParam(name = "registrationRequestBody", value = "Request body") final @RequestBody SimpleObject registrationRequestBody) {
    final PropertyValues registrationProperties = new MutablePropertyValues(
        registrationRequestBody);
    final Patient patient = cflRegistrationUiService.createOrUpdatePatient(registrationProperties);

    final List<Relationship> patientRelationships = cflRegistrationUiService.parseRelationships(
        registrationProperties);

    final RegistrationData registrationData = new RegistrationData();
    registrationData.setPatient(patient);
    registrationData.setRelationships(patientRelationships);
    getLocationFromAttribute(patient).ifPresent(registrationData::setIdentifierLocation);
    final Patient registeredPatient = registrationCoreService.registerPatient(registrationData);

    createPatientTemplatesIfNeeded(patient);

    flashInfoMessage(registrationRequest, patient);

    return new ResponseEntity<>(registeredPatient.getUuid(), HttpStatus.OK);
  }

  private Optional<Location> getLocationFromAttribute(Patient patient) {
    final String locationAttributeName =
        administrationService.getGlobalProperty(CFLConstants.PERSON_LOCATION_ATTRIBUTE_KEY);

    if (locationAttributeName == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(patient.getAttribute(locationAttributeName))
        .map(PersonAttribute::getValue)
        .map(locationService::getLocationByUuid);
  }

  private void flashInfoMessage(final ServletRequest registrationRequest, final Patient patient) {
    registrationRequest.setAttribute("emr.infoMessage",
        uiUtils.message("cfl.createdPatientMessage",
            uiUtils.encodeHtml(patient.getPersonName().toString())));
    registrationRequest.setAttribute("emr.toastMessage", "true");
  }

  @ApiOperation(value = "Update Patient", notes = "Update Patient")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful updating Patient"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to update Patient"),
      @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in updating Patient")})
  @RequestMapping(value = "/patientRegistration", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<String> updatePatient(
      @ApiParam(name = "registrationRequestBody", value = "Request body") final @RequestBody SimpleObject registrationRequestBody) {
    final Object patientUuidRaw = registrationRequestBody.get("uuid");

    if (!(patientUuidRaw instanceof String)) {
      throw new APIException("Invalid Patient UUID, expected String but got: " + patientUuidRaw);
    }

    final PropertyValues registrationProperties = new MutablePropertyValues(
        registrationRequestBody);
    final Patient patientToUpdate = cflRegistrationUiService.createOrUpdatePatient(
        registrationProperties);
    patientService.savePatient(patientToUpdate);

    final List<Relationship> patientRelationships = cflRegistrationUiService.parseRelationships(
        registrationProperties);
    cflRegistrationUiService.updateRelationships(patientToUpdate, patientRelationships);

    return new ResponseEntity<>(patientToUpdate.getUuid(), HttpStatus.OK);
  }

  private void createPatientTemplatesIfNeeded(Patient patient) {
    String gp = GlobalPropertyUtils.getGlobalProperty(
        CFLConstants.CREATION_PATIENT_TEMPLATES_AFTER_REGISTRATION_GP_KEY);
    String[] gpValues = gp.split(",");
    if (Boolean.parseBoolean(gpValues[SHOULD_CREATE_PATIENT_TEMPLATES_INDEX])) {
      List<PatientTemplate> patientTemplates = getDefaultPatientTemplateService().generateDefaultPatientTemplates(
          patient);
      patientTemplates.forEach(patientTemplate -> Context.getService(
              TemplateFieldValueService.class)
          .updateTemplateFieldValue(patientTemplate.getId(),
              MessagesConstants.CHANNEL_TYPE_PARAM_NAME,
              gpValues[DEFAULT_PATIENT_TEMPLATES_CHANNEL_TYPE_INDEX]));
    }
  }

  private DefaultPatientTemplateService getDefaultPatientTemplateService() {
    return Context.getRegisteredComponent("messages.defaultPatientTemplateService",
        DefaultPatientTemplateService.class);
  }
}
