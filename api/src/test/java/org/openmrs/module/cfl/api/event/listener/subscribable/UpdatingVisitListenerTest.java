package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.openmrs.LocationAttributeType;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.helper.VisitHelper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
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
public class UpdatingVisitListenerTest extends VaccinationListenerBaseTest {
    private final Date visitStartDate = new Date(1621528680000L); // 2021-05-20T16:38:00UTC
    private final Date expectedSecondVisit = new Date(1621787880000L); // 2021-05-23T16:38:00UTC
    private final Date expectedThirdVisit = new Date(1622133480000L); // 2021-05-27T16:38:00UTC

    @InjectMocks
    private UpdatingVisitListener updatingVisitListener;

    @Test
    public void performAction_shouldCreateFutureVisits() throws JMSException {
        //Given
        visit = VisitHelper.createVisit(1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_OCCURRED,
                visitStartDate);

        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        CountrySetting countrySetting = new CountrySetting();
        countrySetting.setShouldCreateFutureVisit(true);
        countrySettingMap.put(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY, countrySetting);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
        when(configService.getVaccinationProgram(visit.getPatient())).thenReturn(vaccinations[0].getName());
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME)).thenReturn(true);

        when(visitService.getVisitByUuid(visit.getUuid())).thenReturn(visit);
        when(visitService.getVisitsByPatient(patient)).thenReturn(VisitHelper.getVisits(visit));
        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());

        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn(
                Constant.VISIT_STATUS_OCCURRED);

        when(patientService.getPatient(person.getPersonId())).thenReturn(patient);

        when(locationService.getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME)).thenReturn(
                new LocationAttributeType());

        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(visit.getUuid());

        //When
        updatingVisitListener.performAction(message);
        //Then
        verifyInteractions();
        verify(visitService, times(10)).getAllVisitAttributeTypes();

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
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME)).thenReturn(true);

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
    public void performAction_shouldNotCreateFutureVisitsIfListenerIsDisabled() {
        //Given
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME)).thenReturn(false);

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        //When
        updatingVisitListener.performAction(message);
        //Then
        verify(configService, times(1)).isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME);
        verifyZeroInteractions(visitService);
        verifyZeroInteractions(administrationService);
        verifyZeroInteractions(patientService);
        verifyZeroInteractions(locationService);
    }

    @Test
    public void performAction_shouldNotCreateFutureVisitsIfVisitStatusIsNotOccurred() throws JMSException {
        //Given
        visit = new Visit();

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME)).thenReturn(true);

        when(visitService.getVisitByUuid(visit.getUuid())).thenReturn(visit);

        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(visit.getUuid());
        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn(
                Constant.VISIT_STATUS_OCCURRED);
        //When
        updatingVisitListener.performAction(message);
        //Then
        verify(configService, times(1)).isVaccinationInfoIsEnabled();
        verify(message, times(1)).getString(CFLConstants.UUID_KEY);
        verify(visitService, times(1)).getVisitByUuid(visit.getUuid());
        verify(administrationService, times(1)).getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
        verifyZeroInteractions(patientService);
        verifyZeroInteractions(locationService);
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionIfEncounterNotFoundForGivenVisitUuid() throws JMSException {
        //Given
        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME)).thenReturn(true);
        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.VISIT_UUID);
        when(visitService.getVisitByUuid(anyString())).thenReturn(null);
        //When
        try {
            updatingVisitListener.performAction(message);
            Assert.fail("Unable to retrieve visit by uuid: null");
        } catch (CflRuntimeException e) {
            //Then
            verify(configService, times(1)).isVaccinationInfoIsEnabled();
            verify(message, times(1)).getString(CFLConstants.UUID_KEY);
            verify(visitService, times(1)).getVisitByUuid(anyString());
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

        when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
        when(configService.getVaccinationProgram(visit.getPatient())).thenReturn(vaccinations[0].getName());
        when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME)).thenReturn(true);

        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn(
                Constant.VISIT_STATUS_OCCURRED);

        when(visitService.getVisitsByPatient(patient)).thenReturn(VisitHelper.getVisits(visit));
        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());
        when(visitService.getVisitByUuid(visit.getUuid())).thenReturn(visit);

        when(patientService.getPatient(person.getPersonId())).thenReturn(patient);

        when(locationService.getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME)).thenReturn(
                new LocationAttributeType());

        when(message.getString(CFLConstants.UUID_KEY)).thenReturn(visit.getUuid());

        //When
        updatingVisitListener.performAction(message);
        //Then
        verifyInteractions();
        verify(visitService, times(1)).getAllVisitAttributeTypes();
    }

    private void verifyInteractions() throws JMSException {
        verify(configService, times(1)).isVaccinationInfoIsEnabled();
        verify(message, times(1)).getString(CFLConstants.UUID_KEY);
        verify(visitService, times(1)).getVisitByUuid(visit.getUuid());
        verify(configService, times(1)).getRandomizationGlobalProperty();
        verify(configService, times(1)).getVaccinationProgram(visit.getPatient());
        verify(visitService, times(1)).getVisitsByPatient(patient);
        verify(configService, times(1)).getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY);
        verify(patientService, times(1)).getPatient(person.getPersonId());
        verify(locationService, times(1)).getLocationAttributeTypeByName(CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME);
        verify(administrationService, times(1)).getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
    }

    private Randomization createRandomization() {
        return new Randomization(vaccinations);
    }
}
