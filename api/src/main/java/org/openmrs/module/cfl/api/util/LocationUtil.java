package org.openmrs.module.cfl.api.util;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;

public final class LocationUtil {

  public static Location getPatientLocation(Patient patient) {
    Location patientLocation = null;
    PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
    if (patientIdentifier != null) {
      patientLocation = patientIdentifier.getLocation();
    }

    return patientLocation;
  }

  private LocationUtil() {
  }
}
