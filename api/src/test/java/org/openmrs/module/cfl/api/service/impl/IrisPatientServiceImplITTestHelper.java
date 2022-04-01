package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.service.IrisPatientService;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class IrisPatientServiceImplITTestHelper {

    private static final String PATIENT_NAME = "Arek";
    private static final String PATIENT_GENDER = "M";
    private static final String PATIENT_UPDATED_GENDER = "F";
    private static final int ID_OF_UPDATED_PATIENT = 6;

    private IrisPatientService irisPatientService;

    @Rollback(false)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewPatient() {
        final Patient patient = new Patient();
        patient.setGender(PATIENT_GENDER);
        patient.addIdentifier(new PatientIdentifier("PHL-PGH-000014",
                Context.getPatientService().getPatientIdentifierTypeByUuid("41e81bef-5847-4222-8e0b-8917a5f613d8"),
                Context.getLocationService().getLocation(1)));
        patient.getPerson().addName(new PersonName(PATIENT_NAME, PATIENT_NAME, PATIENT_NAME));
        irisPatientService.saveOrUpdatePatient(patient);
    }

    @Rollback(false)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAlreadyExistingPatient() {
        final Patient patient = Context.getPatientService().getPatient(ID_OF_UPDATED_PATIENT);
        patient.setGender(PATIENT_UPDATED_GENDER);
        irisPatientService.saveOrUpdatePatient(patient);
    }

    public void setIrisPatientService(IrisPatientService irisPatientService) {
        this.irisPatientService = irisPatientService;
    }
}
