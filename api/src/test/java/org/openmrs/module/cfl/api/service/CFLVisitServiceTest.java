package org.openmrs.module.cfl.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.helper.PatientHelper;
import org.openmrs.module.cfl.api.service.impl.CFLVisitServiceImpl;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Context.class} )
public class CFLVisitServiceTest {

    private final CFLVisitService cflVisitService = new CFLVisitServiceImpl();

    private List<Patient> patients;

    @Mock
    private VaccinationService vaccinationService;

    @Before
    public void setUp() {
        mockStatic(Context.class);

        when(Context.getRegisteredComponent(CFLConstants.VACCINATION_SERVICE_BEAN_NAME, VaccinationService.class))
                .thenReturn(vaccinationService);
    }

    @Test
    public void shouldNotInvokeRescheduleVisitsMethodWhenPatientsListIsEmpty() {
        patients = new ArrayList<>();

        cflVisitService.rescheduleVisitsByPatients(patients);

        verifyZeroInteractions(vaccinationService);

        verifyStatic(times(0));
        Context.flushSession();
        Context.clearSession();
    }

    @Test
    public void shouldInvokeRescheduleVisitsMethod5Times() {
        patients = PatientHelper.buildPatientList(5);

        cflVisitService.rescheduleVisitsByPatients(patients);

        verify(vaccinationService, times(5)).rescheduleRegimenVisitsByPatient(any(Patient.class));

        verifyStatic(times(5));
        Context.flushSession();
        Context.clearSession();
    }
}
