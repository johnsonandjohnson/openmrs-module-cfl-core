package org.openmrs.module.cfl.api.helper;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.module.cfl.Constant;

import java.util.HashSet;
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

    private PatientHelper() {
    }

}
