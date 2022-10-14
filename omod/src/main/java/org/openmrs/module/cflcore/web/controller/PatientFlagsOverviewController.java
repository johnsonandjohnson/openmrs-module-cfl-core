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
import javax.persistence.EntityNotFoundException;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.dto.PatientFlagsOverviewDTO;
import org.openmrs.module.cflcore.api.service.PatientFlagsOverviewService;
import org.openmrs.module.cflcore.domain.criteria.PatientFlagsOverviewCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Patient flags overview API", tags = "REST API for operations related to flagged patients")
@RestController("cfl.patientFlagsOverviewController")
@RequestMapping(value = "/cfl/patientFlags")
public class PatientFlagsOverviewController {

  @ApiOperation(value = "None", notes = "Gets patients by specific criteria")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Patients have been successfully fetched"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failed to fetch patients")
  })
  @RequestMapping(value = "/{locationUuid}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<PatientFlagsOverviewDTO> getPatientsByCriteria(
      @ApiParam(name = "locationUuid", value = "locationUuid") @PathVariable("locationUuid") String locationUuid,
      @ApiParam(name = "flagName", value = "flagName") @RequestParam(required = false) String flagName,
      @ApiParam(name = "patientIdentifier", value = "patientIdentifier") @RequestParam(required = false) String patientIdentifier,
      @ApiParam(name = "patientName", value = "patientName") @RequestParam(required = false) String patientName,
      @ApiParam(name = "phoneNumber", value = "phoneNumber") @RequestParam(required = false) String phoneNumber,
      @ApiParam(name = "patientStatus", value = "patientStatus") @RequestParam(required = false) String patientStatus,
      @ApiParam(name = "pageNumber", value = "pageNumber") @RequestParam(required = false) Integer pageNumber,
      @ApiParam(name = "pageSize", value = "pageSize") @RequestParam(required = false) Integer pageSize) {

    Location location = Context.getLocationService().getLocationByUuid(locationUuid);
    if (location == null) {
      throw new EntityNotFoundException(
          String.format("Location with uuid: %s does not exist", locationUuid));
    }

    PatientFlagsOverviewCriteria criteria = getFlagsOverviewCriteria(locationUuid, flagName,
        patientIdentifier,
        patientName, phoneNumber, patientStatus);

    PatientFlagsOverviewDTO result = Context.getService(PatientFlagsOverviewService.class)
        .getPatientsWithFlag(criteria, pageNumber, pageSize);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  private PatientFlagsOverviewCriteria getFlagsOverviewCriteria(String locationUuid,
      String flagName, String patientIdentifier, String patientName, String phoneNumber,
      String patientStatus) {
    return new PatientFlagsOverviewCriteria(locationUuid, flagName, patientIdentifier, patientName,
        phoneNumber, patientStatus);
  }
}
