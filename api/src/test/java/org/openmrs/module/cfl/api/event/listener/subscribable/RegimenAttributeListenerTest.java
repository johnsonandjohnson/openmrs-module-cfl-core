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
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
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
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Collections;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.module.cfl.CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class RegimenAttributeListenerTest {

    @Mock
    private VaccinationService vaccinationService;

    @Mock
    private PersonService personService;

    @Mock
    private ConfigService configService;

    @Mock
    private PatientService patientService;

    @Mock
    private MapMessage message;

    @Mock
    private VisitService visitService;

    @InjectMocks
    private RegimenAttributeListener regimenAttributeListener;

    private Patient patient;

    @Before
    public void setUp() {
        mockStatic(Context.class);

        Person person = PersonHelper.createPerson();
        Location location = LocationHelper.createLocation();
        patient = PatientHelper.createPatient(person, location);
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionForInvalidEventMessage() {
        //When
        try {
            regimenAttributeListener.performAction(null);
            fail("should throw CflRuntimeException: Event message has to be MapMessage");
        } catch (CflRuntimeException e) {
            //Then
            verifyZeroInteractions(vaccinationService);
        }
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionIfPersonNotFound() {
        //When
        try {
            when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
            when(Context.getPersonService()).thenReturn(personService);
            when(personService.getPersonAttributeByUuid(Constant.PERSON_UUID)).thenReturn(null);
            regimenAttributeListener.performAction(message);
            fail("should throw CflRuntimeException: Unable to retrieve person by uuid");
        } catch (CflRuntimeException | JMSException e) {
            //Then
            verify(personService, times(1)).getPersonAttributeByUuid(anyString());
        }
    }

    @Test
    public void performAction_whenVisitListIsEmpty() throws JMSException {
        Person person = PersonHelper.createPerson();
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        when(personService.getPersonAttributeByUuid(Constant.PERSON_UUID)).thenReturn(person.getAttribute(
                VACCINATION_PROGRAM_ATTRIBUTE_NAME));
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class))
                .thenReturn(configService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(patientService.getPatientByUuid(anyString())).thenReturn(patient);
        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getPersonService()).thenReturn(personService);
        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(Collections.emptyList());
        regimenAttributeListener.performAction(message);
        verify(personService, times(1)).getPersonAttributeByUuid(anyString());
        verifyStatic(times(1));
        Context.getPatientService();
        verifyStatic(times(1));
        Context.getVisitService();
    }

    @Test
    public void performAction_whenVisitListIsNull() throws JMSException {
        Person person = PersonHelper.createPerson();
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        when(Context.getPersonService()).thenReturn(personService);
        when(personService.getPersonAttributeByUuid(Constant.PERSON_UUID)).thenReturn(person.getAttribute(
                VACCINATION_PROGRAM_ATTRIBUTE_NAME));
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class))
                .thenReturn(configService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(patientService.getPatientByUuid(anyString())).thenReturn(patient);
        when(Context.getVisitService()).thenReturn(visitService);
        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(null);
        regimenAttributeListener.performAction(message);
        verify(personService, times(1)).getPersonAttributeByUuid(anyString());
        verify(patientService, times(1)).getPatientByUuid(anyString());
        verify(visitService, times(1)).getActiveVisitsByPatient(any(Patient.class));
    }
}
