package org.openmrs.module.cfl.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.cfl.api.monitor.AllMonitoredStatusData;
import org.openmrs.module.cfl.api.monitor.MonitoringStatus;
import org.openmrs.module.cfl.api.service.MonitoringService;
import org.openmrs.module.cfl.web.monitor.SystemStatusResponseBody;
import org.openmrs.module.cfl.web.monitor.SystemStatusResponseBodyBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The CFL Monitoring REST Controller.
 */
@Controller("cfl.monitoringController")
public class MonitoringController extends BaseCflModuleRestController {

    @Autowired
    private MonitoringService monitoringService;

    @RequestMapping(value = "/monitoring", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SystemStatusResponseBody> getSystemStatus(
            @RequestParam(value = "statusOnly", required = false) String statusOnlyRaw,
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

        return new ResponseEntity<SystemStatusResponseBody>(response, httpStatus);
    }

    private HttpStatus toHttpStatus(final AllMonitoredStatusData statusData) {
        return statusData.getMonitoringStatus() == MonitoringStatus.OK ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
