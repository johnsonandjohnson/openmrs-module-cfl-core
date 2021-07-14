package org.openmrs.module.cfl.web.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.service.CFLPatientService;
import org.openmrs.module.cfl.web.dto.RegimensPatientsDataDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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

        return new ResponseEntity<>(getResultsList(regimenGP), HttpStatus.OK);
    }

    private List<RegimensPatientsDataDTO> getResultsList(String regimenGP) {
        List<RegimensPatientsDataDTO> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(regimenGP)) {
            Randomization randomization = new Randomization(getGson().fromJson(regimenGP, Vaccination[].class));
            for (Vaccination vaccination : randomization.getVaccinations()) {
                List<Patient> patients = getCFLPatientService().findByVaccinationName(vaccination.getName());
                List<String> patientsUuids = getPatientsUuids(patients);
                resultList.add(new RegimensPatientsDataDTO(vaccination.getName(), patientsUuids, patients.size(),
                        patients.size() > 0)
                );
            }
        }
        return resultList;
    }

    private List<String> getPatientsUuids(List<Patient> patients) {
        List<String> patientUuids = new ArrayList<>();
        for (Patient patient : patients) {
            patientUuids.add(patient.getUuid());
        }
        return patientUuids;
    }

    private CFLPatientService getCFLPatientService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_PATIENT_SERVICE_BEAN_NAME, CFLPatientService.class);
    }

    private Gson getGson() {
        return new GsonBuilder().setLenient().create();
    }

}
