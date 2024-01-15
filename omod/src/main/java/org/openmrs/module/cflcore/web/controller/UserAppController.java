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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

@Api(value = "User Apps API", tags = "REST API for operations related to user apps")
@RestController("cfl.userAppController")
@RequestMapping(value = "/cfl/apps")
public class UserAppController {

  @ApiOperation(
      value = "None",
      notes = "Gets specific user apps - mainly required for Multi-project support")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "User app descriptors have been successfully fetched"),
        @ApiResponse(
            code = HttpURLConnection.HTTP_INTERNAL_ERROR,
            message = "Failed to fetch user app descriptors")
      })
  @RequestMapping(value = "", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Object> > getSpecificUserApps() {
    List<Object> filteredApps = Context.getService(AppFrameworkService.class).getAllApps()
        .stream()
        .filter(
            app -> StringUtils.startsWithAny(app.getId(), CFLConstants.REGISTER_PATIENT_APP_NAME,
                CFLConstants.REGISTER_CAREGIVER_APP_NAME, CFLConstants.FIND_PATIENT_APP_NAME,
                CFLConstants.FIND_CAREGIVER_APP_NAME, CFLConstants.PATIENT_FLAGS_OVERVIEW_APP_NAME,
                CFLConstants.FIND_CAREGIVER_APP_NAME, CFLConstants.CONFIGURABLE_PATIENT_HEADER_APP_NAME))
        .map(app -> ConversionUtil.convertToRepresentation(app, Representation.DEFAULT))
        .collect(Collectors.toList());

    return new ResponseEntity<>(filteredApps, HttpStatus.OK);
  }
}
