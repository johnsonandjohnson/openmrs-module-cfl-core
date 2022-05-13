/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
