package org.openmrs.module.cfl.api.util;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.api.context.Context;

public final class PatientUtil {

  public static String getPatientCountry(Patient patient) {
    Location patientLocation = LocationUtil.getPatientLocation(patient);
    return patientLocation != null ? patientLocation.getCountry() : null;
  }

  public static PatientIdentifier getPatientIdentifier(Person person) {
    Patient patient = Context.getPatientService().getPatient(person.getPersonId());
    return patient.getPatientIdentifier();
  }

  private PatientUtil() {
  }
}

