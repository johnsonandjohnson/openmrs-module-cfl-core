package org.openmrs.module.cfl.fragment.controller.dashboardwidgets;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLRegisterPersonConstants;
import org.openmrs.module.cfl.api.builder.PersonDTOBuilder;
import org.openmrs.module.cfl.api.dto.FamilyWidgetDTO;
import org.openmrs.module.cfl.api.dto.PersonDTO;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.List;

/**
 * Controller copied from coreapps module and adapted to handel the person object
 */
public class PersonFamilyWidgetFragmentController {
    
    private static final String PATIENT = "patient";
    
    private static final String PATIENT_ID = "patientId";
    
    private static final Integer DEFAULT_MAX_RECORDS = 15;
    
    public void controller(FragmentConfiguration config, FragmentModel model) {
        Person person = getPerson(config);
        List<Relationship> relationships = getAllRelationshipsByPerson(person);

        FamilyWidgetDTO familyWidgetDTO = new FamilyWidgetDTO();
        List<PersonDTO> relatedPeople = familyWidgetDTO.buildPeopleDTOs(relationships, person);

        int maxRecords = (StringUtils.isNotBlank((String) config.get("maxRecords")))
                ? Integer.parseInt((String) config.get("maxRecords")) : DEFAULT_MAX_RECORDS;

        PersonDTOBuilder personDTOBuilder = new PersonDTOBuilder();

        model.addAttribute("currentPersonLocation", personDTOBuilder.getPersonLocation(person.getPersonId()));
        model.addAttribute("relatedPeople", relatedPeople);
        model.addAttribute("maxRecords", maxRecords);
    }

    private Person getPerson(FragmentConfiguration config) {
        Object person = config.getAttribute(CFLRegisterPersonConstants.PERSON_PROP);
        if (person == null) {
            person = config.get(PATIENT);
        }
        if (person == null && config.containsKey(PATIENT_ID) && config.get(PATIENT_ID) != null) {
            person = Context.getPersonService().getPerson((Integer) config.get(PATIENT_ID));
        }
        if (person instanceof PatientDomainWrapper) {
            PatientDomainWrapper patientWrapper = new PatientDomainWrapper();
            Patient patient = Context.getPatientService().getPatient(((PatientDomainWrapper) person).getId());
            patientWrapper.setPatient(patient);
            return patientWrapper.getPatient();
        }
        return (Person) person;
    }
    
    private List<Relationship> getAllRelationshipsByPerson(Person person) {
        return Context.getPersonService().getRelationshipsByPerson(person);
    }
}
