package org.openmrs.module.cfl.api.event.listener.subscribable;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Person;
import org.openmrs.Patient;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.Visit;
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
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class UpdatingVisitListenerTest {

    @Mock
    private MapMessage message;

    @Mock
    private ConfigService configService;

    @Mock
    private VisitService visitService;

    @Mock
    private AdministrationService administrationService;

    @Mock
    private PatientService patientService;

    @Mock
    private LocationService locationService;

    @Mock
    private IrisVisitService irisVisitService;

    @InjectMocks
    private UpdatingVisitListener updatingVisitListener;

    private Person person;
    private Patient patient;
    private Location location;
    private Visit visit;
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
    public void performAction_shouldCreateFutureVisits() throws JMSException {
        //Given
        visit = VisitHelper.createVisit(1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_OCCURRED);

        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFutureVisit(true);
        countrySettingMap.put(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY, countrySetting);

        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.VISIT_UUID);
        when(visitService.getVisitByUuid(Constant.VISIT_UUID)).thenReturn(visit);
        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn(Constant.VISIT_STATUS_OCCURRED);
        when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
        when(configService.getVaccinationProgram(visit.getPatient())).thenReturn(vaccinations[0].getName());
        when(visitService.getVisitsByPatient(patient)).thenReturn(VisitHelper.getVisits(visit));
        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(patientService.getPatient(person.getPersonId())).thenReturn(patient);
        when(locationService.getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME)).thenReturn(new LocationAttributeType());
        //When
        updatingVisitListener.performAction(message);
        //Then
        verifyInteractions();
        verify(visitService, times(0)).getAllVisitAttributeTypes();
        verify(visitService, times(0)).saveVisit(any(Visit.class));
    }

    @Test
    public void performAction_shouldNotCreateFutureVisitsIfVaccinationInfoIsDisabled() {
        //Given
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(configService.isVaccinationInfoIsEnabled()).thenReturn(false);
        //When
        updatingVisitListener.performAction(message);
        //Then
        verify(configService, times(1)).isVaccinationInfoIsEnabled();
        verifyZeroInteractions(visitService);
        verifyZeroInteractions(administrationService);
        verifyZeroInteractions(patientService);
        verifyZeroInteractions(locationService);
    }

    @Test
    public void performAction_shouldNotCreateFutureVisitsIfVisitStatusIsNotOccurred() throws JMSException {
        //Given
        visit = new Visit();
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getAdministrationService()).thenReturn(administrationService);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.VISIT_UUID);
        when(visitService.getVisitByUuid(Constant.VISIT_UUID)).thenReturn(visit);
        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn(Constant.VISIT_STATUS_OCCURRED);
        //When
        updatingVisitListener.performAction(message);
        //Then
        verify(configService, times(1)).isVaccinationInfoIsEnabled();
        verify(message, times(1)).getString(CFLConstants.UUID_KEY);
        verify(visitService, times(1)).getVisitByUuid(Constant.VISIT_UUID);
        verify(administrationService, times(1)).getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
        verifyZeroInteractions(patientService);
        verifyZeroInteractions(locationService);
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionIfVisitNotFoundForGivenVisitUuid() throws JMSException {
        //Given
        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(Context.getVisitService()).thenReturn(visitService);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.VISIT_UUID);
        when(visitService.getVisitByUuid(Constant.VISIT_UUID)).thenReturn(null);
        //When
        try {
            updatingVisitListener.performAction(message);
            Assert.fail("Unable to retrieve visit by uuid: null");
        } catch (CflRuntimeException e) {
            //Then
            verify(configService, times(1)).isVaccinationInfoIsEnabled();
            verify(message, times(1)).getString(CFLConstants.UUID_KEY);
            verify(visitService, times(1)).getVisitByUuid(Constant.VISIT_UUID);
            verifyZeroInteractions(administrationService);
            verifyZeroInteractions(patientService);
            verifyZeroInteractions(locationService);
        }

    }

    @Test
    public void performAction_shouldNotPrepareDataAndSaveVisitIfCreateFutureVisitIsDisabled() throws JMSException {
        //Given
        visit = VisitHelper.createVisit(1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_OCCURRED);

        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFutureVisit(false);
        countrySettingMap.put(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY, countrySetting);

        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(configService);
        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.VISIT_UUID);
        when(visitService.getVisitByUuid(Constant.VISIT_UUID)).thenReturn(visit);
        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn(Constant.VISIT_STATUS_OCCURRED);
        when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
        when(configService.getVaccinationProgram(visit.getPatient())).thenReturn(vaccinations[0].getName());
        when(visitService.getVisitsByPatient(patient)).thenReturn(VisitHelper.getVisits(visit));
        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(patientService.getPatient(person.getPersonId())).thenReturn(patient);
        when(locationService.getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME)).thenReturn(new LocationAttributeType());
        //When
        updatingVisitListener.performAction(message);
        //Then
        verifyInteractions();
        verify(visitService, times(0)).getAllVisitAttributeTypes();
    }

    private void verifyInteractions() throws JMSException {
        verify(configService, times(1)).isVaccinationInfoIsEnabled();
        verify(message, times(1)).getString(CFLConstants.UUID_KEY);
        verify(visitService, times(1)).getVisitByUuid(Constant.VISIT_UUID);
        verify(administrationService, times(1)).getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
        verify(configService, times(0)).getRandomizationGlobalProperty();
        verify(configService, times(0)).getVaccinationProgram(visit.getPatient());
        verify(visitService, times(0)).getVisitsByPatient(patient);
        verify(configService, times(0)).getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY);
        verify(patientService, times(0)).getPatient(person.getPersonId());
        verify(locationService, times(0)).getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME);
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

}
