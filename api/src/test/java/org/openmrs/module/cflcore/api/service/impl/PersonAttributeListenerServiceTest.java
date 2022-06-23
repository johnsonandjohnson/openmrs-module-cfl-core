/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.VisitService;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.event.Event;
import org.openmrs.module.cflcore.api.helper.PatientHelper;
import org.openmrs.module.cflcore.api.helper.PersonHelper;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.service.VaccinationService;
import org.openmrs.module.cflcore.builder.PersonAttributeBuilder;
import org.openmrs.module.cflcore.builder.PersonAttributeTypeBuilder;
import org.openmrs.test.BaseContextMockTest;

import java.util.Date;

import static java.util.Collections.emptyList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.module.cflcore.CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME;

public class PersonAttributeListenerServiceTest extends BaseContextMockTest {

    private Person person;

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private ConfigService configService;

    @Mock
    private VisitService visitService;

    @Mock
    private VaccinationService vaccinationService;

    @InjectMocks
    private PersonAttributeListenerServiceImpl personAttributeListenerService;

    @Before
    public void setUp(){
        person = PersonHelper.createPerson();

        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(emptyList());
        when(configService.getVaccinationProgram(any(Patient.class))).thenReturn(
                person.getAttribute(VACCINATION_PROGRAM_ATTRIBUTE_NAME).getValue());
    }

    @Test
    public void onPersonAttributeEvent_whenCreatedEventIsTriggeredAndPatientHasOneVaccinationAttribute() {
        final Patient patient = PatientHelper.createPatient(person, null);

        when(patientDAO.getPatientByUuid(person.getUuid())).thenReturn(patient);

        personAttributeListenerService.onPersonAttributeEvent(Event.Action.CREATED,
                person.getAttribute(VACCINATION_PROGRAM_ATTRIBUTE_NAME));

        verify(patientDAO).getPatientByUuid(anyString());
        verify(configService).getVaccinationProgram(patient);
        verify(visitService).getActiveVisitsByPatient(patient);
        verifyZeroInteractions(vaccinationService);
    }

    @Test
    public void onPersonAttributeEvent_whenCreatedEventIsTriggeredAndPatientHasMultipleVaccinationAttributes() {
        person.addAttribute(new PersonAttributeBuilder().withPerson(person).withValue("Covid 2D vaccine")
                .withPersonAttributeType(new PersonAttributeTypeBuilder().withName("Vaccination program").build())
                .withVoided(true).build());

        final Patient patient = PatientHelper.createPatient(person, null);

        when(patientDAO.getPatientByUuid(person.getUuid())).thenReturn(patient);

        personAttributeListenerService.onPersonAttributeEvent(Event.Action.CREATED,
                person.getAttribute(VACCINATION_PROGRAM_ATTRIBUTE_NAME));

        verify(patientDAO).getPatientByUuid(anyString());
        verifyZeroInteractions(configService);
        verifyZeroInteractions(visitService);
        verifyZeroInteractions(vaccinationService);
    }

    @Test
    public void onPersonAttributeEvent_whenUpdatedEventIsTriggeredAndPatientHasOneVaccinationAttribute() {
        final Patient patient = PatientHelper.createPatient(person, null);

        when(patientDAO.getPatientByUuid(person.getUuid())).thenReturn(patient);

        personAttributeListenerService.onPersonAttributeEvent(Event.Action.UPDATED,
                person.getAttribute(VACCINATION_PROGRAM_ATTRIBUTE_NAME));

        verify(patientDAO).getPatientByUuid(anyString());
        verifyZeroInteractions(configService);
        verifyZeroInteractions(visitService);
        verifyZeroInteractions(vaccinationService);
    }

    @Test
    public void onPersonAttributeEvent_whenUpdatedEventIsTriggeredAndPatientHasMultipleVaccinationAttributes() {
        person.addAttribute(new PersonAttributeBuilder().withPerson(person).withValue("Covid 2D vaccine")
                .withPersonAttributeType(new PersonAttributeTypeBuilder().withName("Vaccination program").build())
                .withVoided(true).build());

        final Patient patient = PatientHelper.createPatient(person, null);

        when(patientDAO.getPatientByUuid(person.getUuid())).thenReturn(patient);

        personAttributeListenerService.onPersonAttributeEvent(Event.Action.UPDATED,
                person.getAttribute(VACCINATION_PROGRAM_ATTRIBUTE_NAME));

        verify(patientDAO).getPatientByUuid(anyString());
        verify(configService).getVaccinationProgram(patient);
        verify(visitService).getActiveVisitsByPatient(patient);
        verifyZeroInteractions(vaccinationService);
    }

    @Test
    public void onPersonAttributeEvent_shouldSetDateChangedForCreated() {
        internalShouldSetDateChangedFor(Event.Action.CREATED);
    }

    @Test
    public void onPersonAttributeEvent_shouldSetDateChangedForUpdated() {
        internalShouldSetDateChangedFor(Event.Action.UPDATED);
    }

    @Test
    public void onPersonAttributeEvent_shouldSetDateChangedForVoided() {
        internalShouldSetDateChangedFor(Event.Action.VOIDED);
    }

    private void internalShouldSetDateChangedFor(Event.Action action) {
        final Person person = PersonHelper.createPerson();
        final Patient patient = Mockito.spy(PatientHelper.createPatient(person, null));

        when(patientDAO.getPatientByUuid(person.getUuid())).thenReturn(patient);

        personAttributeListenerService.onPersonAttributeEvent(action,
                person.getAttribute(VACCINATION_PROGRAM_ATTRIBUTE_NAME));

        verify(patientDAO, times(1)).getPatientByUuid(person.getUuid());
        verify(patient, times(1)).setDateChanged(any(Date.class));
        verify(patient, times(1)).setChangedBy(any(User.class));
        verify(patientDAO, times(1)).savePatient(patient);
    }
}
