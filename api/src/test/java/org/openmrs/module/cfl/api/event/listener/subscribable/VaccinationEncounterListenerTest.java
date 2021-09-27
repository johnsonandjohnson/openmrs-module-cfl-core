package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.LocationAttributeType;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.helper.VisitHelper;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class VaccinationEncounterListenerTest extends VaccinationListenerBaseTest {
    private final Date encounterDate = new Date(1620743880000L); // 2021-05-11T14:38:00UTC
    private final Date expectedSecondVisit = new Date(1621003080000L); // 2021-05-14T14:38:00UTC
    private final Date expectedThirdVisit = new Date(1621348680000L); // 2021-05-18T14:38:00UTC

    @Mock
    protected EncounterService encounterService;

    @InjectMocks
    private VaccinationEncounterListener vaccinationEncounterListener;

    private Encounter encounter;

    @Before
    public void setEncounterService() {
        when(Context.getEncounterService()).thenReturn(encounterService);
    }

    @Test
    public void performAction_shouldCreateFutureVisits() throws JMSException {
        //Given
        visit = VisitHelper.createVisit(1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_OCCURRED,
                DateUtil.now());
        encounter = VisitHelper.createEncounter(2, encounterDate, visit);
        visit.addEncounter(encounter);

        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFutureVisit(true);
        countrySettingMap.put(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY, countrySetting);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
        when(configService.getVaccinationProgram(visit.getPatient())).thenReturn(vaccinations[0].getName());
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(configService.getVaccinationEncounterTypeUUIDs()).thenReturn(
                Collections.singleton(encounter.getEncounterType().getUuid()));
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME)).thenReturn(true);

        when(encounterService.getEncounterByUuid(encounter.getUuid())).thenReturn(encounter);

        when(visitService.getVisitByUuid(Constant.VISIT_UUID)).thenReturn(visit);
        when(visitService.getVisitsByPatient(patient)).thenReturn(VisitHelper.getVisits(visit));
        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());

        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn(
                Constant.VISIT_STATUS_OCCURRED);

        when(patientService.getPatient(person.getPersonId())).thenReturn(patient);

        when(locationService.getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME)).thenReturn(
                new LocationAttributeType());

        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(encounter.getUuid());

        //When
        vaccinationEncounterListener.performAction(message);
        //Then
        verifyInteractions();
        verify(configService, times(3)).getRandomizationGlobalProperty();
        verify(configService, times(3)).getVaccinationProgram(visit.getPatient());
        verify(visitService, times(12)).getAllVisitAttributeTypes();

        ArgumentCaptor<Visit> saveVisitArgumentCaptor = ArgumentCaptor.forClass(Visit.class);
        verify(visitService, times(2)).saveVisit(saveVisitArgumentCaptor.capture());

        assertThat(saveVisitArgumentCaptor.getAllValues().size(), is(2));
        assertThat(saveVisitArgumentCaptor.getAllValues(),
                containsInAnyOrder(hasProperty("startDatetime", is(expectedSecondVisit)),
                        hasProperty("startDatetime", is(expectedThirdVisit))));
    }

    @Test
    public void performAction_shouldNotCreateFutureVisitsIfVaccinationInfoIsDisabled() {
        //Given
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME)).thenReturn(true);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(false);
        //When
        vaccinationEncounterListener.performAction(message);
        //Then
        verify(configService).isVaccinationInfoIsEnabled();
        verifyZeroInteractions(visitService);
        verifyZeroInteractions(administrationService);
        verifyZeroInteractions(patientService);
        verifyZeroInteractions(locationService);
    }

    @Test
    public void performAction_shouldNotCreateFutureVisitsIfListenerIsDisabled() {
        //Given
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME)).thenReturn(false);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        //When
        vaccinationEncounterListener.performAction(message);
        //Then
        verify(configService).isVaccinationListenerEnabled(CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME);
        verifyZeroInteractions(visitService);
        verifyZeroInteractions(administrationService);
        verifyZeroInteractions(patientService);
        verifyZeroInteractions(locationService);
    }

    @Test
    public void performAction_shouldNotCreateFutureVisitsIfVisitStatusIsNotOccurred() throws JMSException {
        //Given
        visit = new Visit();
        encounter = VisitHelper.createEncounter(2, encounterDate, visit);
        visit.addEncounter(encounter);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.getVaccinationEncounterTypeUUIDs()).thenReturn(
                Collections.singleton(encounter.getEncounterType().getUuid()));
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME)).thenReturn(true);

        when(encounterService.getEncounterByUuid(encounter.getUuid())).thenReturn(encounter);
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(encounter.getUuid());
        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn(
                Constant.VISIT_STATUS_OCCURRED);
        //When
        vaccinationEncounterListener.performAction(message);
        //Then
        verify(configService).isVaccinationInfoIsEnabled();
        verify(message).getString(CFLConstants.UUID_KEY);
        verify(encounterService).getEncounterByUuid(encounter.getUuid());
        verify(administrationService).getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
        verifyZeroInteractions(patientService);
        verifyZeroInteractions(locationService);
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionIfEncounterNotFoundForGivenVisitUuid() throws JMSException {
        //Given
        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME)).thenReturn(true);
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.VISIT_UUID);
        when(encounterService.getEncounterByUuid(anyString())).thenReturn(null);
        //When
        try {
            vaccinationEncounterListener.performAction(message);
            Assert.fail("Unable to retrieve visit by uuid: null");
        } catch (CflRuntimeException e) {
            //Then
            verify(configService).isVaccinationInfoIsEnabled();
            verify(message).getString(CFLConstants.UUID_KEY);
            verify(encounterService).getEncounterByUuid(anyString());
            verifyZeroInteractions(administrationService);
            verifyZeroInteractions(patientService);
            verifyZeroInteractions(locationService);
        }

    }

    @Test
    public void performAction_shouldNotPrepareDataAndSaveVisitIfCreateFutureVisitIsDisabled() throws JMSException {
        //Given
        visit = VisitHelper.createVisit(1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_OCCURRED,
                DateUtil.now());
        encounter = VisitHelper.createEncounter(2, encounterDate, visit);
        visit.addEncounter(encounter);

        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFutureVisit(false);
        countrySettingMap.put(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY, countrySetting);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.getVaccinationEncounterTypeUUIDs()).thenReturn(
                Collections.singleton(encounter.getEncounterType().getUuid()));
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
        when(configService.getVaccinationProgram(visit.getPatient())).thenReturn(vaccinations[0].getName());
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME)).thenReturn(true);

        when(encounterService.getEncounterByUuid(encounter.getUuid())).thenReturn(encounter);

        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn(
                Constant.VISIT_STATUS_OCCURRED);

        when(visitService.getVisitsByPatient(patient)).thenReturn(VisitHelper.getVisits(visit));
        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());

        when(patientService.getPatient(person.getPersonId())).thenReturn(patient);

        when(locationService.getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME)).thenReturn(
                new LocationAttributeType());

        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(encounter.getUuid());

        //When
        vaccinationEncounterListener.performAction(message);
        //Then
        verifyInteractions();
        verify(configService).getRandomizationGlobalProperty();
        verify(configService).getVaccinationProgram(visit.getPatient());
        verify(visitService).getAllVisitAttributeTypes();
    }

    private void verifyInteractions() throws JMSException {
        verify(configService).isVaccinationInfoIsEnabled();
        verify(message).getString(CFLConstants.UUID_KEY);
        verify(encounterService).getEncounterByUuid(encounter.getUuid());
        verify(visitService).getVisitsByPatient(patient);
        verify(configService).getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY);
        verify(patientService).getPatient(person.getPersonId());
        verify(locationService).getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME);
        verify(administrationService).getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
    }

    private Randomization createRandomization() {
        return new Randomization(vaccinations);
    }
}
