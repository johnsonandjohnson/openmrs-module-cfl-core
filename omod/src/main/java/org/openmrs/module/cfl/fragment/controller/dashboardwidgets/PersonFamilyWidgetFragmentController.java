package org.openmrs.module.cfl.fragment.controller.dashboardwidgets;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Relationship;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.api.context.Context.getAdministrationService;
import static org.openmrs.api.context.Context.getPatientService;
import static org.openmrs.module.cfl.CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_KEY;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_PROP;

/**
 * Controller copied from coreapps module and adapted to handel the person object
 */
public class PersonFamilyWidgetFragmentController {
    
    private static final String PATIENT = "patient";
    
    private static final String PATIENT_ID = "patientId";
    
    private static final Integer DEFAULT_MAX_RECORDS = 5;
    
    public void controller(FragmentConfiguration config, FragmentModel model) {
        Person person = getPerson(config);
        
        List<Relationship> relationships = getAllRelationshipsByPerson(person);
    
        int maxRecords = (StringUtils.isNotBlank((String) config.get("maxRecords")))
                ? Integer.parseInt((String) config.get("maxRecords"))
                : DEFAULT_MAX_RECORDS;
        
        List<Person> relatedPeople = getRelatedPeople(relationships, person);
        if (relatedPeople.size() > maxRecords) {
            relatedPeople.subList(maxRecords, relatedPeople.size()).clear();
        }
        List<String> relationshipNames = getPeopleRelationshipNames(relationships, person);
        if (relationshipNames.size() > maxRecords) {
            relationshipNames.subList(maxRecords, relationshipNames.size()).clear();
        }
        List<String> relatedPeopleIdentifiers = getRelatedPeopleIdentifiers(relationships, person);
        if (relatedPeopleIdentifiers.size() > maxRecords) {
            relatedPeopleIdentifiers.subList(maxRecords, relatedPeopleIdentifiers.size()).clear();
        }
        
        model.addAttribute("relatedPeople", relatedPeople);
        model.addAttribute("relationshipNames", relationshipNames);
        model.addAttribute("relatedPeopleIdentifiers", relatedPeopleIdentifiers);
    }
    
    private Person getPerson(FragmentConfiguration config) {
        Object person = config.getAttribute(PERSON_PROP);
        if (person == null) {
            person = config.get(PATIENT);
        }
        if (person == null && config.containsKey(PATIENT_ID) && config.get(PATIENT_ID) != null) {
            person = Context.getPersonService().getPerson((Integer) config.get(PATIENT_ID));
        }
        if (person instanceof PatientDomainWrapper) {
            PatientDomainWrapper patientWrapper = new PatientDomainWrapper();
            Patient patient = getPatientService().getPatient(((PatientDomainWrapper) person).getId());
            patientWrapper.setPatient(patient);
            return patientWrapper.getPatient();
        }
        return (Person) person;
    }
    
    private List<Relationship> getAllRelationshipsByPerson(Person person) {
        return getPersonService().getRelationshipsByPerson(person);
    }
    
    private List<Person> getRelatedPeople(List<Relationship> relationships, Person person) {
        List<Person> relatedPeople = new ArrayList<Person>();
        for (Relationship r : relationships) {
            if (!r.getPersonA().getVoided() && !r.getPersonB().getVoided()) {
                if (r.getPersonA().getId().equals(person.getId())) {
                    relatedPeople.add(r.getPersonB());
                } else {
                    relatedPeople.add(r.getPersonA());
                }
            }
        }
        return relatedPeople;
    }
    
    private List<String> getPeopleRelationshipNames(List<Relationship> relationships, Person person) {
        List<String> relationshipNames = new ArrayList<String>();
        for (Relationship r : relationships) {
            if (!r.getPersonA().getVoided() && !r.getPersonB().getVoided()) {
                if (r.getPersonA().getId().equals(person.getId())) {
                    relationshipNames.add(r.getRelationshipType().getbIsToA());
                } else {
                    relationshipNames.add(r.getRelationshipType().getaIsToB());
                }
            }
        }
        return relationshipNames;
    }
    
    private List<String> getRelatedPeopleIdentifiers(List<Relationship> relationships, Person person) {
        List<String> peopleIdentifiers = new ArrayList<String>();
        for (Relationship r : relationships) {
            if (!r.getPersonA().getVoided() && !r.getPersonB().getVoided()) {
                if (r.getPersonA().getId().equals(person.getId())) {
                    peopleIdentifiers.add(getPersonOrPatientIdentifier(r.getPersonB()));
                } else {
                    peopleIdentifiers.add(getPersonOrPatientIdentifier(r.getPersonA()));
                }
            }
        }
        return peopleIdentifiers;
    }
    
    private String getPersonOrPatientIdentifier(Person person) {
        if (person.isPatient()) {
            Patient patient = getPatientService().getPatient(person.getId());
            return StringUtils.isNotBlank(patient.getPatientIdentifier().getIdentifier())
                    ? patient.getPatientIdentifier().getIdentifier() + " -" : "";
        } else {
            return StringUtils.isNotBlank(getPersonIdentifier(person))
                    ? getPersonIdentifier(person) + " -" : "";
        }
    }
    
    private PersonAttributeType getPersonIdentifierAttributeType() {
        PersonAttributeType type = null;
        String attributeTypeUUID = getAdministrationService().getGlobalProperty(PERSON_IDENTIFIER_ATTRIBUTE_KEY);
        if (StringUtils.isNotBlank(attributeTypeUUID)) {
            type = Context.getPersonService().getPersonAttributeTypeByUuid(attributeTypeUUID);
        }
        return type;
    }

    private String getPersonIdentifier(Person person) {
        String personIdentifier = null;
        PersonAttributeType identifierAttributeType = getPersonIdentifierAttributeType();
        if (identifierAttributeType != null) {
            PersonAttribute attribute = person.getAttribute(identifierAttributeType);
            if (attribute != null) {
                personIdentifier = attribute.getValue();
            }
        }
        return personIdentifier;
    }
    
    private PersonService getPersonService() {
        return Context.getPersonService();
    }
}
