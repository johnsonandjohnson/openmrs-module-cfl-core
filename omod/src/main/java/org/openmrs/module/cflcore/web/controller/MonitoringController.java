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
import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.cflcore.api.monitor.AllMonitoredStatusData;
import org.openmrs.module.cflcore.api.monitor.MonitoringStatus;
import org.openmrs.module.cflcore.api.service.MonitoringService;
import org.openmrs.module.cflcore.web.monitor.SystemStatusResponseBody;
import org.openmrs.module.cflcore.web.monitor.SystemStatusResponseBodyBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.HttpURLConnection;

/**
 * The CFL Monitoring REST Controller.
 */
@Api(value = "CFL Monitoring", tags = {"REST API to Monitor CFL"})
@Controller("cfl.monitoringController")
public class MonitoringController extends BaseCflModuleRestController {

    @Autowired
    private MonitoringService monitoringService;

    @ApiOperation(value = "Get system status", notes = "Get system status")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful getting system status"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to get system status"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in getting system status")})
    @RequestMapping(value = "/monitoring", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SystemStatusResponseBody> getSystemStatus(
            @ApiParam(name = "statusOnly", value = "Status only flag")
            @RequestParam(value = "statusOnly", required = false) String statusOnlyRaw,
            @ApiParam(name = "component", value = "Component")
            @RequestParam(value = "component", required = false) String component) {
        final boolean statusOnly = Boolean.parseBoolean(statusOnlyRaw);
        final AllMonitoredStatusData statusData;

        if (StringUtils.isEmpty(component)) {
            statusData = monitoringService.getStatus();
        } else {
            statusData = monitoringService.getStatus(component);
        }

        final HttpStatus httpStatus;
        final SystemStatusResponseBody response;

        if (statusData == null) {
            httpStatus = HttpStatus.OK;
            response = SystemStatusResponseBodyBuilder.statusOk();
        } else if (statusOnly) {
            httpStatus = toHttpStatus(statusData);
            response = SystemStatusResponseBodyBuilder.withStatusOnly(statusData);
        } else {
            httpStatus = toHttpStatus(statusData);
            response = SystemStatusResponseBodyBuilder.withAll(statusData);
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    private HttpStatus toHttpStatus(final AllMonitoredStatusData statusData) {
        return statusData.getMonitoringStatus() == MonitoringStatus.OK ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
