/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.event.listener.subscribable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.Constant;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.cflcore.api.helper.LocationHelper;
import org.openmrs.module.cflcore.api.helper.PatientHelper;
import org.openmrs.module.cflcore.api.helper.PersonHelper;
import org.openmrs.module.cflcore.api.helper.VisitHelper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.MapMessage;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Context.class} )
public class VisitEncounterListenerTest {

    @InjectMocks
    private VisitEncounterListener visitEncounterListener;

    @Mock
    private EncounterService encounterService;

    @Mock
    private VisitService visitService;

    @Mock
    private MapMessage message;

    private final Date visitDate = new Date(1626769800000L); //2021-07-20 10:30:00

    private final Date encounterDate = new Date(1627651800000L); //2021-07-30 15:30:00

    @Before
    public void setUp() {
        mockStatic(Context.class);

        when(Context.getEncounterService()).thenReturn(encounterService);
        when(Context.getVisitService()).thenReturn(visitService);
    }

    @Test(expected = CflRuntimeException.class)
    public void performAction_shouldThrowExceptionWhenEncounterNotFound() {
        when(encounterService.getEncounterByUuid(anyString())).thenReturn(null);

        visitEncounterListener.performAction(message);

        verify(encounterService, times(1)).getEncounterByUuid(anyString());
        verifyZeroInteractions(visitService);
    }

    @Test
    public void performAction_shouldNotUpdateEncounterVisitWhenVisitDoesNotExist() {
        Encounter encounter = VisitHelper.createEncounter(1, encounterDate, null);
        when(encounterService.getEncounterByUuid(anyString())).thenReturn(encounter);

        visitEncounterListener.performAction(message);

        verify(encounterService, times(1)).getEncounterByUuid(anyString());
        verifyZeroInteractions(visitService);
        assertNull(encounter.getVisit());
    }

    @Test
    public void performAction_shouldUpdateEncounterVisitWhenVisitExists() {
        Patient patient = PatientHelper.createPatient(PersonHelper.createPerson(), LocationHelper.createLocation());
        Visit visit = VisitHelper.createVisit(1, patient, Constant.VISIT_TYPE_DOSING,
                Constant.VISIT_STATUS_OCCURRED, visitDate);
        Encounter encounter = VisitHelper.createEncounter(1, encounterDate, visit);
        when(encounterService.getEncounterByUuid(anyString())).thenReturn(encounter);
        when(Context.getAuthenticatedUser()).thenReturn(buildTestUser());

        assertNull(encounter.getVisit().getDateChanged());
        assertNull(encounter.getVisit().getChangedBy());

        visitEncounterListener.performAction(message);

        verify(encounterService, times(1)).getEncounterByUuid(anyString());
        verify(visitService, times(1)).saveVisit(any(Visit.class));
        assertNotNull(encounter.getVisit().getDateChanged());
        assertNotNull(encounter.getVisit().getChangedBy());
    }

    private User buildTestUser() {
        User user = new User(1);
        user.setSystemId(Constant.ADMIN);
        user.setUsername(Constant.ADMIN);
        return user;
    }
}
