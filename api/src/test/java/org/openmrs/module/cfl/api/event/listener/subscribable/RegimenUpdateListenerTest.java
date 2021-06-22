/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Person;
import org.openmrs.Patient;
import org.openmrs.Location;
import org.openmrs.Visit;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.helper.LocationHelper;
import org.openmrs.module.cfl.api.helper.PatientHelper;
import org.openmrs.module.cfl.api.helper.PersonHelper;
import org.openmrs.module.cfl.api.helper.VisitHelper;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.IrisVisitService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class RegimenUpdateListenerTest {

    @Mock
    private IrisVisitService irisVisitService;

    @Mock
    private PersonService personService;

    @Mock
    private ConfigService configService;

    @Mock
    private PatientService patientService;

    @Mock
    private AdministrationService administrationService;

    @Mock
    private MapMessage message;

    @Mock
    private VisitService visitService;

    @InjectMocks
    private RegimenUpdateListener regimenUpdateListener;

    private Person person;
    private Patient patient;
    private Location location;

    @Before
    public void setUp() throws IOException {
        mockStatic(Context.class);

        person = PersonHelper.createPerson();
        location = LocationHelper.createLocation();
        patient = PatientHelper.createPatient(person, location);
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionForInvalidEventMessage() {
        //When
        try {
            regimenUpdateListener.performAction(null);
            fail("should throw CflRuntimeException: Event message has to be MapMessage");
        } catch (CflRuntimeException e) {
            //Then
            verifyZeroInteractions(irisVisitService);
        }
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionIfPersonNotFound() {
        //When
        try {
            when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
            when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(null);
            regimenUpdateListener.performAction(message);
            fail("should throw CflRuntimeException: Unable to retrieve person by uuid");
        } catch (CflRuntimeException | JMSException e) {
            //Then
            verify(personService, times(1)).getPersonByUuid(anyString());
        }
    }

    @Test
    public void performAction_whenVisitListIsEmpty() throws JMSException {
        Person person = PersonHelper.createPerson();
        PersonHelper.updatePersonWithRegimenAttribute(person);
        List<Visit> visitList = new ArrayList<>();
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(patientService.getPatientByUuid(anyString())).thenReturn(patient);
        when(Context.getVisitService()).thenReturn(visitService);
        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(Collections.emptyList());
        regimenUpdateListener.performAction(message);
        verify(personService, times(1)).getPersonByUuid(anyString());
        verify(patientService, times(1)).getPatientByUuid(anyString());
        verify(visitService,times(1)).getActiveVisitsByPatient(any(Patient.class));
    }

    @Test
    public void performAction_whenVisitListIsNull() throws JMSException {
        Person person = PersonHelper.createPerson();
        PersonHelper.updatePersonWithRegimenAttribute(person);
        List<Visit> visitList = new ArrayList<>();
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(patientService.getPatientByUuid(anyString())).thenReturn(patient);
        when(Context.getVisitService()).thenReturn(visitService);
        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(null);
        regimenUpdateListener.performAction(message);
        verify(personService, times(1)).getPersonByUuid(anyString());
        verify(patientService, times(1)).getPatientByUuid(anyString());
        verify(visitService,times(1)).getActiveVisitsByPatient(any(Patient.class));

    }

    @Test
    public void performAction_whenOldRegimenIsNull() throws JMSException {
        Person person = PersonHelper.createPerson();
        PersonHelper.updatePersonWithRegimenAttribute(person);
        List<Visit> visitList = new ArrayList<>();
        Visit visit = VisitHelper.createVisit(1, patient, "DOSING", "SCHEDULED");
        visitList.add(visit);
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(patientService.getPatientByUuid(anyString())).thenReturn(patient);
        when(Context.getVisitService()).thenReturn(visitService);
        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(visitList);
        when(configService.getVaccinationProgram(any(Person.class))).thenReturn("COVID VACCINE");
        List<VisitAttributeType> visitAttributeTypeList = VisitHelper.createVisitAttrTypes();
        when(visitService.getAllVisitAttributeTypes()).thenReturn(visitAttributeTypeList);
        regimenUpdateListener.performAction(message);
        verify(personService, times(1)).getPersonByUuid(anyString());
        verify(patientService, times(1)).getPatientByUuid(anyString());
        verify(visitService,times(1)).getActiveVisitsByPatient(any(Patient.class));
        verify(configService, times(1)).getVaccinationProgram(any(Person.class));
    }

    @Test
    public void performAction_whenLastOccurredVisitExists() throws JMSException {
        Person person = PersonHelper.createPerson();
        PersonHelper.updatePersonWithOldAndNewRegimenAttribute(person);
        List<Visit> visitList = new ArrayList<>();
        Visit visit = VisitHelper.createVisit(1, patient, "Dosing", "SCHEDULED");
        visitList.add(visit);
        visit = VisitHelper.createVisit(2, patient, "Dosing", "OCCURRED");
        visitList.add(visit);
        visit = VisitHelper.createVisit(3, patient, "Dosing", "OCCURRED");
        visitList.add(visit);
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(patientService.getPatientByUuid(anyString())).thenReturn(patient);
        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(visitList);
        when(configService.getVaccinationProgram(any(Person.class))).thenReturn("COVID VACCINE");
        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn("OCCURRED");
        List<VisitAttributeType> visitAttributeTypeList = VisitHelper.createVisitAttrTypes();
        when(visitService.getAllVisitAttributeTypes()).thenReturn(visitAttributeTypeList);
        regimenUpdateListener.performAction(message);
        verify(personService, times(1)).getPersonByUuid(anyString());
        verify(patientService, times(1)).getPatientByUuid(anyString());
        verify(visitService,times(1)).getActiveVisitsByPatient(any(Patient.class));
        verify(configService, times(1)).getVaccinationProgram(any(Person.class));
    }

}
