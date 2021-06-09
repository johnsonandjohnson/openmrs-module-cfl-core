package org.openmrs.module.cfl.api.event.listener.subscribable;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.helper.LocationHelper;
import org.openmrs.module.cfl.api.helper.PatientHelper;
import org.openmrs.module.cfl.api.helper.PersonHelper;
import org.openmrs.module.cfl.api.helper.VisitHelper;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VisitReminderService;
import org.openmrs.module.cfl.api.service.WelcomeService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;
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

    @Mock
    private MapMessage message;

    @Mock
    private PersonService personService;

    @Mock
    private WelcomeService welcomeService;

    @Mock
    private ConfigService configService;

    @Mock
    private PatientService patientService;

    @Mock
    private LocationService locationService;

    @Mock
    private VisitService visitService;

    @Mock
    private VisitReminderService visitReminderService;

    @InjectMocks
    private RegisteringPeopleListener registeringPeopleListener;

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
    }

    @Test
    public void performAction_shouldPerformActionPostRegisteringPeopleWithoutLocationAttribute() throws JMSException {
        //Given
        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFirstVisit(true);
        countrySettingMap.put(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY, countrySetting);

        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(
                configService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);
        when(Context.getVisitService()).thenReturn(visitService);

        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        doNothing().when(welcomeService).sendWelcomeMessages(person);
        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.getVaccinationProgram(person)).thenReturn(vaccinations[0].getName());
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
        when(locationService.getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME)).thenReturn(
                new LocationAttributeType());
        when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
        when(patientService.getPatient(person.getPersonId())).thenReturn(patient);
        when(patientService.getPatientByUuid(Constant.PERSON_UUID)).thenReturn(patient);
        when(visitService.getAllVisitTypes()).thenReturn(VisitHelper.getVisitTypes());
        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());
        doNothing().when(visitReminderService).create(person);

        //When
        registeringPeopleListener.performAction(message);
        //Then
        verifyInteractions();
    }

    @Test
    public void performAction_shouldPerformActionPostRegisteringPeopleWithLocationAttribute() throws JMSException {
        //Given
        PersonHelper.updatePersonWithLocationAttribute(person);
        patient = PatientHelper.createPatient(person, location);

        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFirstVisit(true);
        countrySettingMap.put(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY, countrySetting);

        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(
                configService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);
        when(Context.getVisitService()).thenReturn(visitService);

        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        doNothing().when(welcomeService).sendWelcomeMessages(person);
        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.getVaccinationProgram(person)).thenReturn(vaccinations[0].getName());
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
        when(locationService.getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME)).thenReturn(
                new LocationAttributeType());
        when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
        when(patientService.getPatient(person.getPersonId())).thenReturn(patient);
        when(patientService.getPatientByUuid(Constant.PERSON_UUID)).thenReturn(patient);
        when(visitService.getAllVisitTypes()).thenReturn(VisitHelper.getVisitTypes());
        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());
        when(locationService.getLocationByUuid(Constant.LOCATION_UUID)).thenReturn(location);
        doNothing().when(visitReminderService).create(person);

        //When
        registeringPeopleListener.performAction(message);
        //Then
        verify(locationService, times(1)).getLocationByUuid(Constant.LOCATION_UUID);
        verifyInteractions();
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionIfPersonNotFound() {
        //When
        try {
            registeringPeopleListener.performAction(message);
            fail("should throw CflRuntimeException: Unable to retrieve person by uuid");
        } catch (CflRuntimeException e) {
            //Then
            verify(personService, times(1)).getPersonByUuid(null);
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
        //When
        try {
            registeringPeopleListener.performAction(null);
            fail("should throw CflRuntimeException: Event message has to be MapMessage");
        } catch (CflRuntimeException e) {
            //Then
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
    public void performAction_shouldNotCreateFirstVisitIfVaccinationInfoIsDisable() throws JMSException {
        //Given
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(
                configService);

        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
        doNothing().when(welcomeService).sendWelcomeMessages(person);
        when(configService.isVaccinationInfoIsEnabled()).thenReturn(false);
        //When
        registeringPeopleListener.performAction(message);
        //Then
        verify(message, times(1)).getString(CFLConstants.UUID_KEY);
        verify(personService, times(1)).getPersonByUuid(Constant.PERSON_UUID);
        verify(welcomeService, times(1)).sendWelcomeMessages(person);
        verify(configService, times(1)).isVaccinationInfoIsEnabled();
        verifyZeroInteractions(patientService);
        verifyZeroInteractions(locationService);
        verifyZeroInteractions(visitService);
        verifyZeroInteractions(visitReminderService);
    }

    @Test
    public void performAction_shouldNotCreateFirstVisitIfCreateFirstVisitIsDisable() throws JMSException {
        //Given
        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFirstVisit(false);
        countrySettingMap.put(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY, countrySetting);

        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(
                configService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);

        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
        doNothing().when(welcomeService).sendWelcomeMessages(person);
        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.getVaccinationProgram(person)).thenReturn(vaccinations[0].getName());
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(patientService.getPatient(person.getPersonId())).thenReturn(patient);
        when(locationService.getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME)).thenReturn(
                new LocationAttributeType());
        //When
        registeringPeopleListener.performAction(message);
        verify(message, times(1)).getString(CFLConstants.UUID_KEY);
        verify(personService, times(1)).getPersonByUuid(Constant.PERSON_UUID);
        verify(welcomeService, times(1)).sendWelcomeMessages(person);
        verify(configService, times(1)).isVaccinationInfoIsEnabled();
        verify(configService, times(1)).getVaccinationProgram(person);
        verify(configService, times(1)).getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY);
        verify(patientService, times(1)).getPatient(person.getPersonId());
        verify(locationService, times(1)).getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME);
        verify(visitReminderService, times(1)).create(person);
        verifyZeroInteractions(visitService);
    }

    @Test
    public void performAction_shouldScheduleVisitEvenIfSendWelcomeMessageFailed() throws JMSException {
        //Given
        PersonHelper.updatePersonWithLocationAttribute(person);
        patient = PatientHelper.createPatient(person, location);

        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFirstVisit(true);
        countrySettingMap.put(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY, countrySetting);

        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(
                configService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);
        when(Context.getVisitService()).thenReturn(visitService);

        doThrow(SendWelcomeMessageException.class).when(welcomeService).sendWelcomeMessages(person);

        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.PERSON_UUID);
        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.getVaccinationProgram(person)).thenReturn(vaccinations[0].getName());
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
        when(locationService.getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME)).thenReturn(
                new LocationAttributeType());
        when(personService.getPersonByUuid(Constant.PERSON_UUID)).thenReturn(person);
        when(patientService.getPatient(person.getPersonId())).thenReturn(patient);
        when(patientService.getPatientByUuid(Constant.PERSON_UUID)).thenReturn(patient);
        when(visitService.getAllVisitTypes()).thenReturn(VisitHelper.getVisitTypes());
        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());
        when(locationService.getLocationByUuid(Constant.LOCATION_UUID)).thenReturn(location);
        doNothing().when(visitReminderService).create(person);

        //When
        try {
            registeringPeopleListener.performAction(message);
        } catch (SendWelcomeMessageException swme) {
            fail("The SendWelcomeMessageException should not be thrown!");
        }

        //Then
        verify(locationService, times(1)).getLocationByUuid(Constant.LOCATION_UUID);
        verifyInteractions();
    }

    private void verifyInteractions() throws JMSException {
        verify(message, times(1)).getString(CFLConstants.UUID_KEY);
        verify(personService, times(1)).getPersonByUuid(Constant.PERSON_UUID);
        verify(welcomeService, times(1)).sendWelcomeMessages(person);
        verify(configService, times(1)).isVaccinationInfoIsEnabled();
        verify(configService, times(1)).getVaccinationProgram(person);
        verify(configService, times(1)).getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY);
        verify(configService, times(1)).getRandomizationGlobalProperty();
        verify(patientService, times(1)).getPatient(person.getPersonId());
        verify(patientService, times(1)).getPatientByUuid(Constant.PERSON_UUID);
        verify(locationService, times(1)).getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME);
        verify(visitService, times(1)).getAllVisitTypes();
        verify(visitService, times(4)).getAllVisitAttributeTypes();
        verify(visitReminderService, times(1)).create(person);
    }

    private Randomization createRandomization() {
        return new Randomization(vaccinations);
    }

    private Vaccination[] createVaccination() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(Constant.COVID_VACCINATION_PROGRAM_JSON_FILE);
        Vaccination vaccination = new Gson().fromJson(IOUtils.toString(in), Vaccination.class);
        Vaccination[] vaccinations = {vaccination};
        return vaccinations;
    }

    public static class SendWelcomeMessageException extends RuntimeException {
        private static final long serialVersionUID = 6128744967307911161L;
    }
}
