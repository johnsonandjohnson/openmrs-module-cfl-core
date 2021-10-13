package org.openmrs.module.cfl.api.service;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.module.cfl.db.ExtendedPatientDataDAO;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CFLPatientServiceTest extends BaseModuleContextSensitiveTest {

    @Autowired
    @Qualifier("cfl.patientService")
    private CFLPatientService cflPatientService;

    @Autowired
    @Qualifier("cfl.extendedPatientDataDAO")
    private ExtendedPatientDataDAO extendedPatientDataDAO;

    @Before
    public void setUp() throws Exception {
        executeDataSet("datasets/CFLPatientServiceTest.xml");
    }

    @Test
    public void shouldFind3PatientsByVaccination_1ProgramName() {
        List<Patient> patients = cflPatientService.findByVaccinationName("Vaccination_1");
        assertThat(patients, is(not(empty())));
        assertEquals(3, patients.size());
    }

    @Test
    public void shouldFind2PatientsByVaccination_2ProgramName() {
        List<Patient> patients = cflPatientService.findByVaccinationName("Vaccination_2");
        assertThat(patients, is(not(empty())));
        assertEquals(2, patients.size());
    }

    @Test
    public void shouldFind1PatientByVaccination_3ProgramName() {
        List<Patient> patients = cflPatientService.findByVaccinationName("Vaccination_3");
        assertThat(patients, is(not(empty())));
        assertEquals(1, patients.size());
    }

    @Test
    public void shouldNotFindAnyPatientsByVaccination_4ProgramName() {
        List<Patient> patients = cflPatientService.findByVaccinationName("Vaccination_4");
        assertThat(patients, is((empty())));
    }

    @Test
    public void shouldReturnFourDifferentNonVoidedVaccinesTypes() {
        List<String> vaccinesTypes = extendedPatientDataDAO.getVaccineNamesLinkedToAnyPatient();

        assertEquals(4, vaccinesTypes.size());
        assertTrue(isVaccinesListContainsVaccineName(vaccinesTypes, "Vaccination_1"));
        assertTrue(isVaccinesListContainsVaccineName(vaccinesTypes, "Vaccination_2"));
        assertTrue(isVaccinesListContainsVaccineName(vaccinesTypes, "Vaccination_3"));
        assertFalse(isVaccinesListContainsVaccineName(vaccinesTypes, "Vaccination_4"));
        assertTrue(isVaccinesListContainsVaccineName(vaccinesTypes, "Vaccination_5"));
    }

    private boolean isVaccinesListContainsVaccineName(List<String> vaccinesTypes, String vaccineName) {
        return vaccinesTypes.contains(vaccineName);
    }
}
