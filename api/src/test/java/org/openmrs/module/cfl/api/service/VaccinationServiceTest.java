package org.openmrs.module.cfl.api.service;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.impl.VaccinationServiceImpl;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Context.class, Daemon.class} )
public class VaccinationServiceTest {

    private static final String RANDOMIZATION = "Randomization.json";

    private static final String RANDOMIZATION_UPDATED = "RandomizationUpdated.json";

    @Mock
    private CFLPatientService cflPatientService;

    @Mock
    private VisitService visitService;

    @InjectMocks
    private VaccinationServiceImpl vaccinationService;

    private List<Patient> patients;

    @Before
    public void setUp() throws IOException {
        mockStatic(Context.class);

        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getRegisteredComponent(CFLConstants.CFL_PATIENT_SERVICE_BEAN_NAME, CFLPatientService.class))
                .thenReturn(cflPatientService);
        when(Context.getRegisteredComponent(CFLConstants.VACCINATION_SERVICE_BEAN_NAME, VaccinationService.class))
                .thenReturn(vaccinationService);

        patients = buildTestPatientsList();
    }

    @Test
    public void rescheduleVisitsBasedOnRegimenChanges_whenVaccinesAreDifferentAndNotNull() throws IOException {
        when(cflPatientService.findByVaccinationName(anyString())).thenReturn(buildTestPatientsList());

        String randomization = jsonToString(RANDOMIZATION);
        String randomizationUpdated = jsonToString(RANDOMIZATION_UPDATED);
        vaccinationService.rescheduleVisitsBasedOnRegimenChanges(randomization, randomizationUpdated);
        verify(cflPatientService, times(1)).findByVaccinationName(anyString());
        verify(visitService, times(5)).getActiveVisitsByPatient(any(Patient.class));
    }

    @Test
    public void rescheduleVisitsBasedOnRegimenChanges_whenVaccinesValuesAreNull() {
        vaccinationService.rescheduleVisitsBasedOnRegimenChanges(null, null);
        verifyZeroInteractions(cflPatientService);
        verifyZeroInteractions(visitService);
    }

    private String jsonToString(String jsonFile) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFile);
        return IOUtils.toString(in);
    }

    private List<Patient> buildTestPatientsList() {
        return Arrays.asList(
                buildPatient(1),
                buildPatient(2),
                buildPatient(3),
                buildPatient(4),
                buildPatient(5));
    }

    private Patient buildPatient(Integer id) {
        Patient patient = new Patient();
        patient.setPatientId(id);
        return patient;
    }
}
