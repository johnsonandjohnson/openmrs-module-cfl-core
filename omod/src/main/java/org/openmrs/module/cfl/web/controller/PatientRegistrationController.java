package org.openmrs.module.cfl.web.controller;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Relationship;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.web.service.CFLRegistrationUiService;
import org.openmrs.module.registrationcore.RegistrationData;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Optional;

@Controller("cfl.patientRegistrationController")
public class PatientRegistrationController extends BaseCflModuleRestController {

    @Autowired
    private RegistrationCoreService registrationCoreService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private CFLRegistrationUiService cflRegistrationUiService;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UiUtils uiUtils;

    @RequestMapping(value = "/patientRegistration", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> registerPatient(final ServletRequest registrationRequest,
                                                  final @RequestBody SimpleObject registrationRequestBody) {
        final PropertyValues registrationProperties = new MutablePropertyValues(registrationRequestBody);
        final Patient patient = cflRegistrationUiService.createOrUpdatePatient(registrationProperties);

        final List<Relationship> patientRelationships = cflRegistrationUiService.parseRelationships(registrationProperties);

        final RegistrationData registrationData = new RegistrationData();
        registrationData.setPatient(patient);
        registrationData.setRelationships(patientRelationships);
        getLocationFromAttribute(patient).ifPresent(registrationData::setIdentifierLocation);
        final Patient registeredPatient = registrationCoreService.registerPatient(registrationData);

        flashInfoMessage(registrationRequest, patient);

        return new ResponseEntity<>(registeredPatient.getUuid(), HttpStatus.OK);
    }

    private Optional<Location> getLocationFromAttribute(Patient patient) {
        final String locationAttributeName =
                administrationService.getGlobalProperty(CFLConstants.PERSON_LOCATION_ATTRIBUTE_KEY);

        if (locationAttributeName == null) {
            return Optional.empty();
        }

        final String locationUuid = patient.getAttribute(locationAttributeName).getValue();
        return Optional.ofNullable(locationService.getLocationByUuid(locationUuid));
    }

    private void flashInfoMessage(final ServletRequest registrationRequest, final Patient patient) {
        registrationRequest.setAttribute("emr.infoMessage",
                uiUtils.message("cfl.createdPatientMessage", uiUtils.encodeHtml(patient.getPersonName().toString())));
        registrationRequest.setAttribute("emr.toastMessage", "true");
    }

    @RequestMapping(value = "/patientRegistration", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updatePatient(final @RequestBody SimpleObject registrationRequestBody) {
        final Object patientUuidRaw = registrationRequestBody.get("uuid");

        if (!(patientUuidRaw instanceof String)) {
            throw new APIException("Invalid Patient UUID, expected String but got: " + patientUuidRaw);
        }

        final PropertyValues registrationProperties = new MutablePropertyValues(registrationRequestBody);
        final Patient patientToUpdate = cflRegistrationUiService.createOrUpdatePatient(registrationProperties);
        patientService.savePatient(patientToUpdate);

        final List<Relationship> patientRelationships = cflRegistrationUiService.parseRelationships(registrationProperties);
        cflRegistrationUiService.updateRelationships(patientToUpdate, patientRelationships);

        return new ResponseEntity<>(patientToUpdate.getUuid(), HttpStatus.OK);
    }

}
