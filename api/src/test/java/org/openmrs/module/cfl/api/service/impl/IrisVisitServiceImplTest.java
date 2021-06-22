/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.service.impl;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAddress;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.helper.LocationHelper;
import org.openmrs.module.cfl.api.helper.PatientHelper;
import org.openmrs.module.cfl.api.helper.PersonHelper;
import org.openmrs.module.cfl.api.helper.VisitHelper;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class IrisVisitServiceImplTest {

    @Mock
    private VisitService visitService;

    @Mock
    private ConfigService configService;

    @Mock
    private AdministrationService administrationService;

    @Mock
    private PatientService patientService;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private IrisVisitServiceImpl irisVisitService;

    private Vaccination[] vaccinations;

    @Before
    public void setUp() throws IOException {
        mockStatic(Context.class);
    }

    @Test
    public void voidFutureVisits_shouldVoidAllTheVisits() {
        Person person = PersonHelper.createPerson();
        Location location = LocationHelper.createLocation();
        Patient patient = PatientHelper.createPatient(person, location);
        List<VisitAttributeType> visitAttributeTypeList = VisitHelper.createVisitAttrTypes();
        int visitId = 1;
        String visitType = "Dosing";
        String visitStatus = "SCHEDULED";
        Visit visit = VisitHelper.createVisit(visitId, patient, visitType, visitStatus);
        List<Visit> visits = new ArrayList<Visit>();
        visits.add(VisitHelper.createVisit(1, patient, "DOSE 1 VISIT", "OCCURRED"));
        visits.add(visit);
        when(Context.getVisitService()).thenReturn(visitService);
        when(visitService.getActiveVisitsByPatient(any(Patient.class))).thenReturn(visits);
        when(Context.getVisitService().getAllVisitAttributeTypes()).thenReturn(visitAttributeTypeList);
        irisVisitService.voidFutureVisits(patient);
        verify(visitService, times(1)).voidVisit(any(Visit.class), any(String.class));
        verify(visitService, times(1)).getActiveVisitsByPatient(any(Patient.class));
    }

    @Test
    public void createFutureVisits_shouldNotCreateFutureVisitsWhenShouldCreateFutureVisitsFlagIsFalse() throws IOException {

        vaccinations = createVaccination(Constant.COVID_VACCINATION_PROGRAM_JSON_FILE);
        Person person = PersonHelper.createPerson();
        PersonAddress personAddress = new PersonAddress();
        personAddress.setCountry("BELGIUM");
        person.addAddress(personAddress);
        Location location = LocationHelper.createLocation();
        Patient patient = PatientHelper.createPatient(person, location);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(Constant.COVID_VACCINATION_PROGRAM_JSON_FILE);
        Vaccination vaccination = new Gson().fromJson(IOUtils.toString(in), Vaccination.class);
        String vaccinationProgram = "Vac_3 (three doses)";
        String visitTypeName = "DOSING";
        int doseNumber = 2;
        List<Visit> visits = new ArrayList<Visit>();
        List<VisitAttributeType> visitAttributeTypeList = VisitHelper.createVisitAttrTypes();
        Visit visit = VisitHelper.createVisit(1, patient, "DOSE 1 VISIT", "OCCURRED");
        visits.add(visit);
        visits.add(VisitHelper.createVisit(1, patient, "DOSE 2 VISIT", "SCHEDULED"));
        Randomization randomization = createRandomization();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFutureVisit(false);
        VisitInformation visitInformation = new VisitInformation();
        visitInformation.setNameOfDose(visitTypeName);
        visitInformation.setDoseNumber(doseNumber);
        when(Context.getVisitService()).thenReturn(visitService);
        when(visitService.getAllVisitAttributeTypes()).thenReturn(visitAttributeTypeList);
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(configService.getRandomizationGlobalProperty()).thenReturn(randomization);
        when(configService.getVaccinationProgram(visits.get(0).getPatient())).thenReturn(vaccinationProgram);
        when(Context.getVisitService().getVisitsByPatient(patient)).thenReturn(visits);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);
        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn("OCCURRED");
        when(patientService.getPatient(anyInt())).thenReturn(patient);
        when(Context.getLocationService().getLocationAttributeTypeByName(anyString())).thenReturn(LocationHelper.createLocationAttributeType("Country decoded"));
        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        countrySettingMap.put("BELGIUM", countrySetting);
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        irisVisitService.createFutureVisits(visits.get(1));
        verify(visitService, times(2)).getAllVisitAttributeTypes();
        verify(configService, times(1)).getRandomizationGlobalProperty();
        verify(administrationService, times(1)).getGlobalProperty(anyString());
        verify(patientService, times(1)).getPatient(anyInt());
        verify(configService, times(1)).getCountrySettingMap(anyString());
    }

    @Test
    public void createFutureVisits_shouldCreateFutureVisitsWhenShouldCreateFutureVisitsFlagIsTrueAndPatientAttribute() throws IOException {

        vaccinations = createVaccination("COVIDNEW.json");
        Person person = PersonHelper.createPerson();
        PersonAddress personAddress = new PersonAddress();
        personAddress.setCountry("BELGIUM");
        person.addAddress(personAddress);
        Location location = LocationHelper.createLocation();
        Patient patient = PatientHelper.createPatient(person, location);
        PersonAttribute personAttribute = PersonHelper.createPersonAttribute(CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE, "newLocation");
        person.addAttribute(personAttribute);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(Constant.COVID_VACCINATION_PROGRAM_JSON_FILE);
        Vaccination vaccination = new Gson().fromJson(IOUtils.toString(in), Vaccination.class);
        String vaccinationProgram = "Vac_3 (three doses)";
        String visitTypeName = "DOSING";
        int doseNumber = 2;
        List<Visit> visits = new ArrayList<Visit>();
        List<VisitAttributeType> visitAttributeTypeList = VisitHelper.createVisitAttrTypes();
        Visit visit = VisitHelper.createVisit(1, patient, "DOSE 1 VISIT", "OCCURRED");
        visits.add(visit);
        visits.add(VisitHelper.createVisit(1, patient, "DOSE 1 VISIT", "SCHEDULED"));
        Randomization randomization = createRandomization();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFutureVisit(true);
        VisitInformation visitInformation = new VisitInformation();
        visitInformation.setNameOfDose(visitTypeName);
        visitInformation.setDoseNumber(doseNumber);
        when(Context.getVisitService()).thenReturn(visitService);
        when(visitService.getAllVisitAttributeTypes()).thenReturn(visitAttributeTypeList);
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(configService.getRandomizationGlobalProperty()).thenReturn(randomization);
        when(configService.getVaccinationProgram(visits.get(0).getPatient())).thenReturn(vaccinationProgram);
        when(Context.getVisitService().getVisitsByPatient(patient)).thenReturn(visits);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);
        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn("OCCURRED");
        when(patientService.getPatient(anyInt())).thenReturn(patient);
        when(Context.getLocationService().getLocationAttributeTypeByName(anyString())).thenReturn(LocationHelper.createLocationAttributeType("Country decoded"));
        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        countrySettingMap.put("BELGIUM", countrySetting);
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(visitService.saveVisit(any(Visit.class))).thenReturn(visit);
        irisVisitService.createFutureVisits(visits.get(1));
        verify(visitService, times(6)).getAllVisitAttributeTypes();
        verify(configService, times(1)).getRandomizationGlobalProperty();
        verify(administrationService, times(1)).getGlobalProperty(anyString());
        verify(patientService, times(1)).getPatient(anyInt());
        verify(configService, times(1)).getCountrySettingMap(anyString());
    }

    @Test
    public void createFutureVisits_shouldCreateFutureVisitsWhenShouldCreateFutureVisitsFlagIsTrue() throws IOException {

        vaccinations = createVaccination("COVIDNEW.json");
        Person person = PersonHelper.createPerson();
        PersonAddress personAddress = new PersonAddress();
        personAddress.setCountry("BELGIUM");
        person.addAddress(personAddress);
        Location location = LocationHelper.createLocation();
        Patient patient = PatientHelper.createPatient(person, location);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(Constant.COVID_VACCINATION_PROGRAM_JSON_FILE);
        String vaccinationProgram = "Vac_3 (three doses)";
        String visitTypeName = "DOSING";
        int doseNumber = 2;
        List<Visit> visits = new ArrayList<Visit>();
        List<VisitAttributeType> visitAttributeTypeList = VisitHelper.createVisitAttrTypes();
        Visit visit = VisitHelper.createVisit(1, patient, "DOSE 1 VISIT", "OCCURRED");
        visits.add(visit);
        visits.add(VisitHelper.createVisit(1, patient, "DOSE 1 VISIT", "SCHEDULED"));
        Randomization randomization = createRandomization();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFutureVisit(true);
        VisitInformation visitInformation = new VisitInformation();
        visitInformation.setNameOfDose(visitTypeName);
        visitInformation.setDoseNumber(doseNumber);
        when(Context.getVisitService()).thenReturn(visitService);
        when(visitService.getAllVisitAttributeTypes()).thenReturn(visitAttributeTypeList);
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(configService.getRandomizationGlobalProperty()).thenReturn(randomization);
        when(configService.getVaccinationProgram(visits.get(0).getPatient())).thenReturn(vaccinationProgram);
        when(Context.getVisitService().getVisitsByPatient(patient)).thenReturn(visits);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);
        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn("OCCURRED");
        when(patientService.getPatient(anyInt())).thenReturn(patient);
        when(Context.getLocationService().getLocationAttributeTypeByName(anyString())).thenReturn(LocationHelper.createLocationAttributeType("Country decoded"));
        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        countrySettingMap.put("BELGIUM", countrySetting);
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(visitService.saveVisit(any(Visit.class))).thenReturn(visit);
        irisVisitService.createFutureVisits(visits.get(1));
        verify(visitService, times(6)).getAllVisitAttributeTypes();
        verify(configService, times(1)).getRandomizationGlobalProperty();
        verify(administrationService, times(1)).getGlobalProperty(anyString());
        verify(patientService, times(1)).getPatient(anyInt());
        verify(configService, times(1)).getCountrySettingMap(anyString());
        verify(visitService, times(1)).saveVisit(any(Visit.class));
    }

    private Randomization createRandomization() {
        return new Randomization(vaccinations);
    }

    private Vaccination[] createVaccination(String fileName) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
        Vaccination vaccination = new Gson().fromJson(IOUtils.toString(in), Vaccination.class);
        Vaccination[] vaccinations = {vaccination};
        return vaccinations;
    }

}
