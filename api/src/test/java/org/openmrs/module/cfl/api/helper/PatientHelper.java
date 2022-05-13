/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
