package org.openmrs.module.cflcore.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cflcore.CFLConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "User Apps API", tags = "REST API for operations related to user apps")
@RestController("cfl.userAppController")
@RequestMapping(value = "/cfl/apps")
public class UserAppController {

  @ApiOperation(value = "None", notes = "Gets all CFL user app descriptors related to register and find patient/caregiver")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "User app descriptors have been successfully fetched"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failed to fetch user app descriptors")
  })
  @RequestMapping(value = "", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<AppDescriptor>> getAllCFLRegisterAndFindAppDescriptors() {
    List<AppDescriptor> allDescriptors = Context.getService(AppFrameworkService.class).getAllApps()
        .stream()
        .filter(
            app -> StringUtils.startsWithAny(app.getId(), CFLConstants.REGISTER_PATIENT_APP_NAME,
                CFLConstants.REGISTER_CAREGIVER_APP_NAME, CFLConstants.FIND_PATIENT_APP_NAME,
                CFLConstants.FIND_CAREGIVER_APP_NAME))
        .collect(Collectors.toList());

    return new ResponseEntity<>(allDescriptors, HttpStatus.OK);
  }
}
