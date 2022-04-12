package org.openmrs.module.cfl.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.dto.RegimensPatientsDataDTO;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * The CFL Regimen REST Controller
 */
@Api(value = "Regimens", tags = {"REST API for Regimens"})
@Controller("cfl.regimenController")
@RequestMapping("/cfl/regimens")
public class RegimenController {

    @ApiOperation(value = "Regimens Patients information", notes = "Regimens Patients information")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful getting regimens Patients info"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to get regimens Patients info"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in getting regimens Patients info")})
    @RequestMapping(value = "/patient-info", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<RegimensPatientsDataDTO>> getRegimensPatientsInfo() {

        String configGP = Context.getAdministrationService().getGlobalProperty(CFLConstants.MAIN_CONFIG);
        List<RegimensPatientsDataDTO> resultList = getVaccinationService().getRegimenResultsList(configGP);

        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    private VaccinationService getVaccinationService() {
        return Context.getRegisteredComponent(CFLConstants.VACCINATION_SERVICE_BEAN_NAME, VaccinationService.class);
    }
}
