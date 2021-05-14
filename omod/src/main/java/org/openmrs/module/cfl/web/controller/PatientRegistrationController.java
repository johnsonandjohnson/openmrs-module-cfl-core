package org.openmrs.module.cfl.web.controller;

import org.openmrs.Patient;
import org.openmrs.Relationship;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.module.cfl.web.service.CFLRegistrationUiService;
import org.openmrs.module.registrationcore.RegistrationData;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller("cfl.patientRegistrationController")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PatientRegistrationController extends BaseCflModuleRestController {
    @Autowired
    private RegistrationCoreService registrationCoreService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private CFLRegistrationUiService cflRegistrationUiService;

    @RequestMapping(value = "/patientRegistration", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> registerPatient(final @RequestBody SimpleObject registrationRequestBody) {
        final PropertyValues registrationProperties = new MutablePropertyValues(registrationRequestBody);
        final Patient patient = cflRegistrationUiService.createOrUpdatePatient(registrationProperties);

        final List<Relationship> patientRelationships = cflRegistrationUiService.parseRelationships(registrationProperties);

        final RegistrationData registrationData = new RegistrationData();
        registrationData.setPatient(patient);
        registrationData.setRelationships(patientRelationships);
        final Patient registeredPatient = registrationCoreService.registerPatient(registrationData);

        return new ResponseEntity<String>(registeredPatient.getUuid(), HttpStatus.OK);
    }

    @RequestMapping(value = "/patientRegistration", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updatePatient(final @RequestBody SimpleObject registrationRequestBody) {
        final Object patientUuidRaw = registrationRequestBody.get("uuid");

        if (!(patientUuidRaw instanceof String)) {
            throw new APIException("Missing Patient UUID.");
        }

        final PropertyValues registrationProperties = new MutablePropertyValues(registrationRequestBody);
        final Patient patientToUpdate = cflRegistrationUiService.createOrUpdatePatient(registrationProperties);
        patientService.savePatient(patientToUpdate);

        final List<Relationship> patientRelationships = cflRegistrationUiService.parseRelationships(registrationProperties);
        cflRegistrationUiService.updateRelationships(patientToUpdate, patientRelationships);

        return new ResponseEntity<String>(patientToUpdate.getUuid(), HttpStatus.OK);
    }

}
