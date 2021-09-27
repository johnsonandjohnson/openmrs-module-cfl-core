package org.openmrs.module.cfl.api.helper;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.module.cfl.Constant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PatientHelper {

    public static Patient createPatient(Person person, Location location) {
        Patient patient = new Patient(person);
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        Set<PatientIdentifier> identifiers = new HashSet<PatientIdentifier>();
        patientIdentifier.setLocation(location);
        identifiers.add(patientIdentifier);
        patient.setIdentifiers(identifiers);
        patient.setUuid(Constant.PATIENT_UUID);
        return patient;
    }

    public static Patient buildPatient(Integer id) {
        Patient patient = new Patient();
        patient.setPatientId(id);
        return patient;
    }

    public static List<Patient> buildPatientList(Integer numberOfPatients) {
        List<Patient> patientList = new ArrayList<>();
        for (int i = 0; i < numberOfPatients; i++) {
            patientList.add(buildPatient(i + 1));
        }
        return patientList;
    }

    private PatientHelper() {
    }

}
