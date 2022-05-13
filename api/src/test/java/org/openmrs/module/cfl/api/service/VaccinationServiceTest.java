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

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.dto.RegimensPatientsDataDTO;
import org.openmrs.module.cfl.api.helper.LocationHelper;
import org.openmrs.module.cfl.api.helper.PatientHelper;
import org.openmrs.module.cfl.api.helper.PersonHelper;
import org.openmrs.module.cfl.api.helper.VisitHelper;
import org.openmrs.module.cfl.api.service.impl.VaccinationServiceImpl;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, Daemon.class})
public class VaccinationServiceTest {

    private final Date visitStartDate = new Date(1621528680000L);

    private static final String RANDOMIZATION = "Randomization.json";

    private static final String RANDOMIZATION_UPDATED = "RandomizationUpdated.json";

    private static final String CFL_VACCINES = "CFLVaccines.json";

    private static final String CONFIG_JSON = "Config.json";

    private static final String VAC_001 = "vac001";

    private static final String VAC_002 = "vac002";

    private static final String VAC_003 = "vac003";

    private final VaccinationService vaccinationService = new VaccinationServiceImpl();

    @Mock
    private CFLPatientService cflPatientService;

    @Mock
    private VisitService visitService;

    @Mock
    private AdministrationService administrationService;

    @Mock
    private CFLVisitService cflVisitService;

    private PersonAttributeType vaccinationProgramAttrType;

    @Before
    public void setUp() throws IOException {
        mockStatic(Context.class);

        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getRegisteredComponent(CFLConstants.CFL_PATIENT_SERVICE_BEAN_NAME, CFLPatientService.class)).thenReturn(
                cflPatientService);
        when(Context.getRegisteredComponent(CFLConstants.CFL_VISIT_SERVICE_BEAN_NAME, CFLVisitService.class)).thenReturn(
                cflVisitService);
        when(cflPatientService.findByVaccinationName(VAC_001)).thenReturn(preparePatientsWithVac001());
        when(cflPatientService.findByVaccinationName(VAC_002)).thenReturn(preparePatientsWithVac002());
        when(cflPatientService.findByVaccinationName(VAC_003)).thenReturn(preparePatientsWithVac003());

        vaccinationProgramAttrType = buildAttributeType(1, "Vaccination program");
    }

    @Test
    public void rescheduleVisitsBasedOnRegimenChanges_whenVaccinesAreDifferentAndNotNull() throws IOException {
        when(cflPatientService.findByVaccinationName(anyString())).thenReturn(buildTestPatientsList());

        String randomization = jsonToString(RANDOMIZATION);
        String randomizationUpdated = jsonToString(RANDOMIZATION_UPDATED);

        vaccinationService.rescheduleVisitsBasedOnRegimenChanges(randomization, randomizationUpdated);

        verify(cflPatientService, times(1)).findByVaccinationName(anyString());
        verify(cflVisitService, times(1)).rescheduleVisitsByPatients(anyListOf(Patient.class));
    }

    @Test
    public void rescheduleVisitsBasedOnRegimenChanges_whenVaccinesValuesAreNull() {
        vaccinationService.rescheduleVisitsBasedOnRegimenChanges(null, null);

        verifyZeroInteractions(cflPatientService);
        verifyZeroInteractions(visitService);
    }

    @Test
    public void rescheduleVisitsBasedOnRegimenChanges_whenVisitsAreNotNull() throws IOException {
        Person person = PersonHelper.createPerson();
        Location location = LocationHelper.createLocation();
        Patient patient = PatientHelper.createPatient(person, location);
        Visit visit = VisitHelper.createVisit(1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_SCHEDULED,
                visitStartDate);
        when(cflPatientService.findByVaccinationName(anyString())).thenReturn(buildTestPatientsList());

        String randomization = jsonToString(RANDOMIZATION);
        String randomizationUpdated = jsonToString(RANDOMIZATION_UPDATED);
        when(Context.getVisitService()).thenReturn(visitService);
        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(Collections.singletonList(visit));
        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.createVisitAttrTypes());
        vaccinationService.rescheduleVisitsBasedOnRegimenChanges(randomization, randomizationUpdated);
        verify(cflPatientService, times(1)).findByVaccinationName(anyString());
        // verify(visitService, times(5)).getActiveVisitsByPatient(any(Patient.class));
    }

    @Test
    public void getRegimensPatientsInfo_whenConfigGPIsBlank() throws IOException {
        when(administrationService.getGlobalProperty(CFLConstants.MAIN_CONFIG)).thenReturn(null);

        List<RegimensPatientsDataDTO> results = vaccinationService.getRegimenResultsList(null);

        verify(cflPatientService, times(0)).findByVaccinationName(anyString());
        assertEquals(0, results.size());
    }

    private List<Patient> preparePatientsWithVac001() {
        Patient patient1 =
                buildPatientWithAttributes(1, false, "f8e41767-e3ee-11eb-8d05-0242ac130001", vaccinationProgramAttrType,
                        VAC_001);
        Patient patient2 =
                buildPatientWithAttributes(2, false, "f8e41767-e3ee-11eb-8d05-0242ac130002", vaccinationProgramAttrType,
                        VAC_001);
        Patient patient3 =
                buildPatientWithAttributes(3, false, "f8e41767-e3ee-11eb-8d05-0242ac130003", vaccinationProgramAttrType,
                        VAC_001);
        return Arrays.asList(patient1, patient2, patient3);
    }

    private List<Patient> preparePatientsWithVac002() {
        Patient patient1 =
                buildPatientWithAttributes(4, false, "f8e41767-e3ee-11eb-8d05-0242ac130004", vaccinationProgramAttrType,
                        VAC_002);
        Patient patient2 =
                buildPatientWithAttributes(5, false, "f8e41767-e3ee-11eb-8d05-0242ac130005", vaccinationProgramAttrType,
                        VAC_002);
        return Arrays.asList(patient1, patient2);
    }

    private List<Patient> preparePatientsWithVac003() {
        return new ArrayList<>();
    }

    private Patient buildPatientWithAttributes(Integer id, boolean isDead, String patientUuid,
                                               PersonAttributeType attributeType, String attributeValue) {
        Person person = buildPerson(id, isDead, patientUuid);
        PersonAttribute personAttribute = buildPersonAttribute(id, person, attributeType, attributeValue);
        person.addAttribute(personAttribute);
        return new Patient(person);
    }

    private PersonAttribute buildPersonAttribute(Integer id, Person person, PersonAttributeType type, String value) {
        PersonAttribute personAttribute = new PersonAttribute();
        personAttribute.setId(id);
        personAttribute.setPerson(person);
        personAttribute.setAttributeType(type);
        personAttribute.setValue(value);
        return personAttribute;
    }

    private Person buildPerson(Integer id, Boolean dead, String uuid) {
        Person person = new Person();
        person.setId(id);
        person.setDead(dead);
        person.setUuid(uuid);
        return person;
    }

    private PersonAttributeType buildAttributeType(Integer id, String name) {
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setId(id);
        personAttributeType.setName(name);
        return personAttributeType;
    }

    private String jsonToString(String jsonFile) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFile);
        return IOUtils.toString(in);
    }

    private List<Patient> buildTestPatientsList() {
        return Arrays.asList(buildPatient(1), buildPatient(2), buildPatient(3), buildPatient(4), buildPatient(5));
    }

    private Patient buildPatient(Integer id) {
        Patient patient = new Patient();
        patient.setPatientId(id);
        return patient;
    }
}
