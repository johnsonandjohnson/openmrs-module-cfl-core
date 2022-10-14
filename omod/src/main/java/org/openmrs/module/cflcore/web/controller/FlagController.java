package org.openmrs.module.cflcore.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.dto.FlagDTO;
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Flags API", tags = "REST API for operations related to flags")
@RestController("cfl.flagController")
@RequestMapping(value = "/cfl/flags")
public class FlagController {

  @ApiOperation(value = "None", notes = "Gets all non-retired flags available in the system")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Flags have been successfully fetched"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failed to fetch flags")
  })
  @RequestMapping(value = "", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<FlagDTO>> getAllFlags() {
    FlagService flagService = Context.getService(FlagService.class);
    List<FlagDTO> flagDTOs = flagService.getAllFlags().stream()
        .filter(flag -> !flag.getRetired())
        .map(flag -> new FlagDTO(flag.getName(), flag.getUuid()))
        .collect(Collectors.toList());

    return new ResponseEntity<>(flagDTOs, HttpStatus.OK);
  }
}
