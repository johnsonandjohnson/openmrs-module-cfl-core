package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.LocationAttribute;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;

public final class PatientUtil {

    public static String getPatientCountry(Person person) {
        String country = "";
        LocationAttribute locationAttribute = LocationUtil.getLocationAttributeByPersonAndAttributeTypeName(person,
                CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME);
        if (locationAttribute != null) {
            country = locationAttribute.getValueReference();
        }
        return country;
    }

    public static PatientIdentifier getPatientIdentifier(Person person) {
        Patient patient = Context.getPatientService().getPatient(person.getPersonId());
        return patient.getPatientIdentifier();
    }

    public static String getPatientFullName(Patient patient) {
        String patientFullName = "";
        if (StringUtils.isNotBlank(patient.getGivenName())) {
            patientFullName += patient.getGivenName() + " ";
        }
        if (StringUtils.isNotBlank(patient.getMiddleName())) {
            patientFullName += patient.getMiddleName() + " ";
        }
        if (StringUtils.isNotBlank(patient.getFamilyName())) {
            patientFullName += patient.getFamilyName();
        }

        return patientFullName;
    }

    private PatientUtil() {
    }
}

