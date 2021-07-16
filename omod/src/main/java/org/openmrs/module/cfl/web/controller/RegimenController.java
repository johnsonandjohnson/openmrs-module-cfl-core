package org.openmrs.module.cfl.web.controller;

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

import java.util.List;

/**
 * The CFL Regimen REST Controller
 */
@Controller("cfl.regimenController")
@RequestMapping("/cfl/regimens")
public class RegimenController {

    @RequestMapping(value = "/patient-info", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<RegimensPatientsDataDTO>> getRegimensPatientsInfo() {
        String regimenGP = Context.getAdministrationService().getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        List<RegimensPatientsDataDTO> resultList = getVaccinationService().getResultsList(regimenGP);

        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    private VaccinationService getVaccinationService() {
        return Context.getRegisteredComponent(CFLConstants.VACCINATION_SERVICE_BEAN_NAME, VaccinationService.class);
    }
}
