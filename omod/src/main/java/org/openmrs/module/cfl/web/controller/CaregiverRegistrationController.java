package org.openmrs.module.cfl.web.controller;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.PersonService;
import org.openmrs.module.cfl.web.service.CFLRegistrationUiService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller("cfl.caregiverRegistrationController")
public class CaregiverRegistrationController extends BaseCflModuleRestController {

    @Autowired
    private PersonService personService;

    @Autowired
    private CFLRegistrationUiService cflRegistrationUiService;

    @RequestMapping(value = "/caregiverRegistration", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public ResponseEntity<String> registerCaregiver(final @RequestBody SimpleObject registrationRequestBody) {
        final PropertyValues registrationProperties = new MutablePropertyValues(registrationRequestBody);

        final Person person = cflRegistrationUiService.createOrUpdatePerson(registrationProperties);
        final Person savedPerson = personService.savePerson(person);

        final List<Relationship> personRelationships = cflRegistrationUiService.parseRelationships(registrationProperties);
        cflRegistrationUiService.updateRelationships(person, personRelationships);

        return new ResponseEntity<>(savedPerson.getUuid(), HttpStatus.OK);
    }
}
