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

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.Constant;
import org.openmrs.module.cflcore.api.contract.Randomization;
import org.openmrs.module.cflcore.api.contract.Vaccination;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.cflcore.api.helper.LocationHelper;
import org.openmrs.module.cflcore.api.helper.PatientHelper;
import org.openmrs.module.cflcore.api.helper.PersonHelper;
import org.openmrs.module.cflcore.api.helper.VisitHelper;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.service.CustomAdministrationService;
import org.openmrs.module.cflcore.api.service.VisitReminderService;
import org.openmrs.module.cflcore.api.service.WelcomeService;
import org.openmrs.module.cflcore.api.util.GlobalPropertiesConstants;
import org.openmrs.module.messages.api.model.CountryProperty;
import org.openmrs.module.messages.api.service.CountryPropertyService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class RegisteringPeopleListenerTest {

  @Mock private MapMessage message;

  @Mock private PersonService personService;

  @Mock private WelcomeService welcomeService;

  @Mock private ConfigService configService;

  @Mock private PatientService patientService;

  @Mock private LocationService locationService;

  @Mock private VisitService visitService;

  @Mock private VisitReminderService visitReminderService;

  @Mock private CountryPropertyService countryPropertyService;

  @Mock private CustomAdministrationService customAdministrationService;

  @InjectMocks private RegisteringPeopleListener registeringPeopleListener;

  private Person person;
  private Patient patient;
  private Location location;
  private Vaccination[] vaccinations;

  @Before
  public void setUp() throws IOException {
    mockStatic(Context.class);

    person = PersonHelper.createPerson();
    location = LocationHelper.createLocation();
    patient = PatientHelper.createPatient(person, location);
    vaccinations = createVaccination();
    when(configService.getRandomizationGlobalProperty())
        .thenReturn(new Randomization(vaccinations));
    when(configService.getVaccinationProgram(patient)).thenReturn("Vac_3 (three doses)");

    when(Context.getService(CountryPropertyService.class)).thenReturn(countryPropertyService);
    when(countryPropertyService.getCountryProperty(any(Concept.class), any(String.class)))
        .thenReturn(Optional.empty());
  }

  @Test
  public void performAction_shouldPerformActionPostRegisteringPeopleWithoutLocationAttribute()
      throws JMSException {
    // Given
    CountryProperty shouldCreateFirstVisitProp = new CountryProperty();
    shouldCreateFirstVisitProp.setName(GlobalPropertiesConstants.SHOULD_CREATE_FIRST_VISIT_GP_KEY);
    shouldCreateFirstVisitProp.setValue("true");

    when(countryPropertyService.getCountryProperty(null, shouldCreateFirstVisitProp.getName()))
        .thenReturn(Optional.of(shouldCreateFirstVisitProp));
    when(Context.getRegisteredComponent(
            CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class))
        .thenReturn(configService);
    when(Context.getPatientService()).thenReturn(patientService);
    when(Context.getLocationService()).thenReturn(locationService);
    when(Context.getVisitService()).thenReturn(visitService);

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
    doNothing().when(welcomeService).sendWelcomeMessages(person);
    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.getVaccinationProgram(person)).thenReturn(vaccinations[0].getName());
    when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
    when(locationService.getLocationAttributeTypeByName(
            CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME))
        .thenReturn(new LocationAttributeType());
    when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
    when(patientService.getPatient(person.getPersonId())).thenReturn(patient);
    when(patientService.getPatientByUuid(Constant.PERSON_UUID)).thenReturn(patient);
    when(visitService.getAllVisitTypes()).thenReturn(VisitHelper.getVisitTypes());
    when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());
    doNothing().when(visitReminderService).create(person);
    when(Context.getService(CustomAdministrationService.class))
        .thenReturn(customAdministrationService);

    // When
    registeringPeopleListener.performAction(message);
    // Then
    verifyInteractions();
  }

  @Test
  public void performAction_shouldPerformActionPostRegisteringPeopleWithLocationAttribute()
      throws JMSException {
    // Given
    PersonHelper.updatePersonWithLocationAttribute(person);
    patient = PatientHelper.createPatient(person, location);

    CountryProperty shouldCreateFirstVisitProp = new CountryProperty();
    shouldCreateFirstVisitProp.setName(GlobalPropertiesConstants.SHOULD_CREATE_FIRST_VISIT_GP_KEY);
    shouldCreateFirstVisitProp.setValue("true");

    when(countryPropertyService.getCountryProperty(null, shouldCreateFirstVisitProp.getName()))
        .thenReturn(Optional.of(shouldCreateFirstVisitProp));
    when(Context.getRegisteredComponent(
            CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class))
        .thenReturn(configService);
    when(Context.getPatientService()).thenReturn(patientService);
    when(Context.getLocationService()).thenReturn(locationService);
    when(Context.getVisitService()).thenReturn(visitService);

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
    doNothing().when(welcomeService).sendWelcomeMessages(person);
    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.getVaccinationProgram(person)).thenReturn(vaccinations[0].getName());
    when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
    when(locationService.getLocationAttributeTypeByName(
            CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME))
        .thenReturn(new LocationAttributeType());
    when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
    when(patientService.getPatient(person.getPersonId())).thenReturn(patient);
    when(patientService.getPatientByUuid(Constant.PERSON_UUID)).thenReturn(patient);
    when(visitService.getAllVisitTypes()).thenReturn(VisitHelper.getVisitTypes());
    when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());
    when(locationService.getLocationByUuid(Constant.LOCATION_UUID)).thenReturn(location);
    doNothing().when(visitReminderService).create(person);
    when(Context.getService(CustomAdministrationService.class))
        .thenReturn(customAdministrationService);

    // When
    registeringPeopleListener.performAction(message);
    // Then
    verifyInteractions();
  }

  @Test
  public void performAction_shouldThrowCflRuntimeExceptionIfPersonNotFound() {
    // When
    try {
      registeringPeopleListener.performAction(message);
      fail("should throw CflRuntimeException: Unable to retrieve person by uuid");
    } catch (CflRuntimeException e) {
      // Then
      verify(personService).getPersonByUuid(null);
      verifyZeroInteractions(welcomeService);
      verifyZeroInteractions(configService);
      verifyZeroInteractions(patientService);
      verifyZeroInteractions(locationService);
      verifyZeroInteractions(visitService);
      verifyZeroInteractions(visitReminderService);
    }
  }

  @Test
  public void performAction_shouldThrowCflRuntimeExceptionForInvalidEventMessage() {
    // When
    try {
      registeringPeopleListener.performAction(null);
      fail("should throw CflRuntimeException: Event message has to be MapMessage");
    } catch (CflRuntimeException e) {
      // Then
      verifyZeroInteractions(personService);
      verifyZeroInteractions(welcomeService);
      verifyZeroInteractions(configService);
      verifyZeroInteractions(patientService);
      verifyZeroInteractions(locationService);
      verifyZeroInteractions(visitService);
      verifyZeroInteractions(visitReminderService);
    }
  }

  @Test
  public void performAction_shouldNotCreateFirstVisitIfVaccinationInfoIsDisable()
      throws JMSException {
    // Given
    when(Context.getRegisteredComponent(
            CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class))
        .thenReturn(configService);

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
    when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
    doNothing().when(welcomeService).sendWelcomeMessages(person);
    when(configService.isVaccinationInfoIsEnabled()).thenReturn(false);
    // When
    registeringPeopleListener.performAction(message);
    // Then
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(personService).getPersonByUuid(Constant.PERSON_UUID);
    verify(welcomeService).sendWelcomeMessages(person);
    verify(configService).isVaccinationInfoIsEnabled();
    verifyZeroInteractions(patientService);
    verifyZeroInteractions(locationService);
    verifyZeroInteractions(visitService);
    verifyZeroInteractions(visitReminderService);
  }

  @Test
  public void performAction_shouldNotCreateFirstVisitIfCreateFirstVisitIsDisable()
      throws JMSException {
    // Given
    CountryProperty shouldCreateFirstVisitProp = new CountryProperty();
    shouldCreateFirstVisitProp.setName(GlobalPropertiesConstants.SHOULD_CREATE_FIRST_VISIT_GP_KEY);
    shouldCreateFirstVisitProp.setValue("false");

    when(countryPropertyService.getCountryProperty(null, shouldCreateFirstVisitProp.getName()))
        .thenReturn(Optional.of(shouldCreateFirstVisitProp));
    when(Context.getRegisteredComponent(
            CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class))
        .thenReturn(configService);
    when(Context.getPatientService()).thenReturn(patientService);
    when(Context.getLocationService()).thenReturn(locationService);

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
    when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
    doNothing().when(welcomeService).sendWelcomeMessages(person);
    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.getVaccinationProgram(person)).thenReturn(vaccinations[0].getName());
    when(patientService.getPatient(person.getPersonId())).thenReturn(patient);
    when(locationService.getLocationAttributeTypeByName(
            CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME))
        .thenReturn(new LocationAttributeType());
    when(Context.getService(CustomAdministrationService.class))
        .thenReturn(customAdministrationService);
    // When
    registeringPeopleListener.performAction(message);
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(personService).getPersonByUuid(Constant.PERSON_UUID);
    verify(welcomeService).sendWelcomeMessages(person);
    verify(configService).isVaccinationInfoIsEnabled();
    verify(configService).getVaccinationProgram(person);
    verify(visitReminderService).create(person);
    verifyZeroInteractions(visitService);
  }

  @Test
  public void performAction_shouldScheduleVisitEvenIfSendWelcomeMessageFailed()
      throws JMSException {
    // Given
    PersonHelper.updatePersonWithLocationAttribute(person);
    patient = PatientHelper.createPatient(person, location);

    CountryProperty shouldCreateFirstVisitProp = new CountryProperty();
    shouldCreateFirstVisitProp.setName(GlobalPropertiesConstants.SHOULD_CREATE_FIRST_VISIT_GP_KEY);
    shouldCreateFirstVisitProp.setValue("true");

    when(countryPropertyService.getCountryProperty(null, shouldCreateFirstVisitProp.getName()))
        .thenReturn(Optional.of(shouldCreateFirstVisitProp));
    when(Context.getRegisteredComponent(
            CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class))
        .thenReturn(configService);
    when(Context.getPatientService()).thenReturn(patientService);
    when(Context.getLocationService()).thenReturn(locationService);
    when(Context.getVisitService()).thenReturn(visitService);

    doThrow(SendWelcomeMessageException.class).when(welcomeService).sendWelcomeMessages(person);

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.getVaccinationProgram(person)).thenReturn(vaccinations[0].getName());
    when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
    when(locationService.getLocationAttributeTypeByName(
            CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME))
        .thenReturn(new LocationAttributeType());
    when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
    when(patientService.getPatient(person.getPersonId())).thenReturn(patient);
    when(patientService.getPatientByUuid(Constant.PERSON_UUID)).thenReturn(patient);
    when(visitService.getAllVisitTypes()).thenReturn(VisitHelper.getVisitTypes());
    when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());
    when(locationService.getLocationByUuid(Constant.LOCATION_UUID)).thenReturn(location);
    doNothing().when(visitReminderService).create(person);
    when(Context.getService(CustomAdministrationService.class))
        .thenReturn(customAdministrationService);

    // When
    try {
      registeringPeopleListener.performAction(message);
    } catch (SendWelcomeMessageException swme) {
      fail("The SendWelcomeMessageException should not be thrown!");
    }

    // Then
    verifyInteractions();
  }

  private void verifyInteractions() throws JMSException {
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(personService).getPersonByUuid(Constant.PERSON_UUID);
    verify(welcomeService).sendWelcomeMessages(person);
    verify(configService).isVaccinationInfoIsEnabled();
    verify(configService).getVaccinationProgram(person);
    verify(configService, times(0)).getRandomizationGlobalProperty();
    verify(patientService).getPatientByUuid(Constant.PERSON_UUID);
    verify(visitReminderService).create(person);
  }

  private Randomization createRandomization() {
    return new Randomization(vaccinations);
  }

  private Vaccination[] createVaccination() throws IOException {
    InputStream in =
        this.getClass()
            .getClassLoader()
            .getResourceAsStream(Constant.COVID_VACCINATION_PROGRAM_JSON_FILE);
    Vaccination vaccination = new Gson().fromJson(IOUtils.toString(in), Vaccination.class);
    return new Vaccination[] {vaccination};
  }

  public static class SendWelcomeMessageException extends RuntimeException {
    private static final long serialVersionUID = 6128744967307911161L;
  }
}
