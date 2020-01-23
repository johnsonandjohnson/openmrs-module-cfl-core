package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.base.Patient;
import model.base.PatientIdentifier;
import model.base.Person;
import model.base.Relationship;
import model.full.FullPatient;
import model.full.RelatedPerson;
import util.FileUtils;
import util.http.BaseHttpResponse;
import util.http.HttpUtils;
import util.http.ResponseResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegistrationService {

    private static final String BASE_URL = "http://cfl-dev-lb-149730096.us-east-1.elb.amazonaws.com/openmrs/ws/rest/v1/";
    private static final String PERSON_URL = BASE_URL + "person";
    private static final String PATIENT_URL = BASE_URL + "patient";
    private static final String RELATIONSHIP_URL = BASE_URL + "relationship";
    private static final String PATIENTS_RESOURCE = "data/patients.json";

    public static void unregisterPatients() {
        for (PatientIdentifier id : getAllIdentifiers()) {
            BaseHttpResponse response = fetchPerson(id.getIdentifier());
            for (ResponseResult person : response.getResults()) {
                deletePerson(person.getUuid());
            }
        }
    }

    public static void initializePatients() {
        unregisterPatients();
        for (FullPatient fullPatient : getAllPatients()) {
            createPatient(fullPatient);
        }
    }

    private static FullPatient createPatient(FullPatient fullPatient) {
        Person person = createPerson(fullPatient.getPerson());
        createRelatedActors(fullPatient.getRelated(), person.getUuid());
        return createPatient(new Patient(person.getUuid(), fullPatient.getIdentifiers()));
    }

    private static void createRelatedActors(List<RelatedPerson> related, String personA) {
        for (RelatedPerson relatedPerson : related) {
            FullPatient patientB = createPatient(relatedPerson.getPersonB());
            createRelationship(
                    new Relationship(personA, patientB.getPerson().getUuid(), relatedPerson.getRelationshipType()));
        }
    }

    private static void createRelationship(Relationship relationship) {
        HttpUtils.post(RELATIONSHIP_URL, relationship);
    }

    private static void deletePerson(String uuid) {
        HttpUtils.delete(PERSON_URL + "/" + uuid + "?reason=Testing purposes");
    }

    private static BaseHttpResponse fetchPerson(String identifier) {
        return HttpUtils.get(PATIENT_URL + "?identifier=" + identifier);
    }

    private static FullPatient createPatient(Patient patient) {
        return HttpUtils.post(PATIENT_URL, patient, FullPatient.class);
    }

    private static List<FullPatient> getAllPatients() {
        return new Gson().fromJson(FileUtils.readFile(PATIENTS_RESOURCE),
                new TypeToken<ArrayList<FullPatient>>() {
                }.getType());
    }

    private static Set<PatientIdentifier> getAllIdentifiers() {
        Set<PatientIdentifier> result = new HashSet<>();
        for (FullPatient patient : getAllPatients()) {
            result.addAll(getAllIdentifiers(patient));
        }
        return result;
    }

    private static Set<PatientIdentifier> getAllIdentifiers(FullPatient fullPatient) {
        Set<PatientIdentifier> result = new HashSet<>(fullPatient.getIdentifiers());
        for (RelatedPerson related : fullPatient.getRelated()) {
            result.addAll(getAllIdentifiers(related.getPersonB()));
        }
        return result;
    }

    private static Person createPerson(Person person) {
        return HttpUtils.post(PERSON_URL, person, Person.class);
    }
}
