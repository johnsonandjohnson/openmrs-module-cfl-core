/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.LocationAttributeType;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.constant.CountryPropertyConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.CountrySettingBuilder;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.helper.VisitHelper;
import org.openmrs.module.messages.api.model.CountryProperty;
import org.openmrs.module.messages.api.service.CountryPropertyService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
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

  @Mock private CountryPropertyService countryPropertyService;

  @InjectMocks private UpdatingVisitListener updatingVisitListener;

  @Before
  public void setEncounterService() {
    when(Context.getService(CountryPropertyService.class)).thenReturn(countryPropertyService);
    when(countryPropertyService.getCountryProperty(any(Concept.class), any(String.class)))
        .thenReturn(Optional.empty());
  }

  @Test
  public void performAction_shouldCreateFutureVisits() throws JMSException {
    // Given
    visit =
        VisitHelper.createVisit(
            1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_OCCURRED, visitStartDate);

    CountryProperty shouldCreateFutureVisitProp = new CountryProperty();
    shouldCreateFutureVisitProp.setName(CountryPropertyConstants.SHOULD_CREATE_FUTURE_VISIT_PROP_NAME);
    shouldCreateFutureVisitProp.setValue("true");

    when(countryPropertyService.getCountryProperty(null, shouldCreateFutureVisitProp.getName()))
        .thenReturn(Optional.of(shouldCreateFutureVisitProp));
    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
    when(configService.getVaccinationProgram(visit.getPatient()))
        .thenReturn(vaccinations[0].getName());
    when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME))
        .thenReturn(true);

    when(visitService.getVisitByUuid(visit.getUuid())).thenReturn(visit);
    when(visitService.getVisitsByPatient(patient)).thenReturn(VisitHelper.getVisits(visit));
    when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());

    when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY))
        .thenReturn(Constant.VISIT_STATUS_OCCURRED);

    when(patientService.getPatient(person.getPersonId())).thenReturn(patient);

    when(locationService.getLocationAttributeTypeByName(
            CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME))
        .thenReturn(new LocationAttributeType());

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(visit.getUuid());

    // When
    updatingVisitListener.performAction(message);
    // Then
    verify(configService).isVaccinationInfoIsEnabled();
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(visitService).getVisitByUuid(visit.getUuid());
    verify(visitService).getVisitsByPatient(patient);
    verify(administrationService).getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
    verify(configService, times(3)).getRandomizationGlobalProperty();
    verify(configService, times(3)).getVaccinationProgram(visit.getPatient());
    verify(visitService, times(12)).getAllVisitAttributeTypes();

    ArgumentCaptor<Visit> saveVisitArgumentCaptor = ArgumentCaptor.forClass(Visit.class);
    verify(visitService, times(2)).saveVisit(saveVisitArgumentCaptor.capture());

    assertThat(saveVisitArgumentCaptor.getAllValues().size(), is(2));
    assertThat(
        saveVisitArgumentCaptor.getAllValues(),
        containsInAnyOrder(
            hasProperty("startDatetime", is(expectedSecondVisit)),
            hasProperty("startDatetime", is(expectedThirdVisit))));
  }

  @Test
  public void performAction_shouldNotCreateFutureVisitsIfVaccinationInfoIsDisabled() {
    // Given
    when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME))
        .thenReturn(true);

    when(configService.isVaccinationInfoIsEnabled()).thenReturn(false);
    // When
    updatingVisitListener.performAction(message);
    // Then
    verify(configService).isVaccinationInfoIsEnabled();
    verifyZeroInteractions(visitService);
    verifyZeroInteractions(administrationService);
    verifyZeroInteractions(patientService);
    verifyZeroInteractions(locationService);
  }

  @Test
  public void performAction_shouldNotCreateFutureVisitsIfListenerIsDisabled() {
    // Given
    when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME))
        .thenReturn(false);

    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    // When
    updatingVisitListener.performAction(message);
    // Then
    verify(configService)
        .isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME);
    verifyZeroInteractions(visitService);
    verifyZeroInteractions(administrationService);
    verifyZeroInteractions(patientService);
    verifyZeroInteractions(locationService);
  }

  @Test
  public void performAction_shouldNotCreateFutureVisitsIfVisitStatusIsNotOccurred()
      throws JMSException {
    // Given
    visit = new Visit();

    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME))
        .thenReturn(true);

    when(visitService.getVisitByUuid(visit.getUuid())).thenReturn(visit);

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(visit.getUuid());
    when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY))
        .thenReturn(Constant.VISIT_STATUS_OCCURRED);
    // When
    updatingVisitListener.performAction(message);
    // Then
    verify(configService).isVaccinationInfoIsEnabled();
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(visitService).getVisitByUuid(visit.getUuid());
    verify(administrationService).getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
    verifyZeroInteractions(patientService);
    verifyZeroInteractions(locationService);
  }

  @Test
  public void performAction_shouldThrowCflRuntimeExceptionIfEncounterNotFoundForGivenVisitUuid()
      throws JMSException {
    // Given
    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME))
        .thenReturn(true);
    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.VISIT_UUID);
    when(visitService.getVisitByUuid(anyString())).thenReturn(null);
    // When
    try {
      updatingVisitListener.performAction(message);
      Assert.fail("Unable to retrieve visit by uuid: null");
    } catch (CflRuntimeException e) {
      // Then
      verify(configService).isVaccinationInfoIsEnabled();
      verify(message).getString(CFLConstants.UUID_KEY);
      verify(visitService).getVisitByUuid(anyString());
      verifyZeroInteractions(administrationService);
      verifyZeroInteractions(patientService);
      verifyZeroInteractions(locationService);
    }
  }

  @Test
  public void performAction_shouldNotPrepareDataAndSaveVisitIfCreateFutureVisitIsDisabled()
      throws JMSException {
    // Given
    visit =
        VisitHelper.createVisit(
            1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_OCCURRED);

    CountryProperty shouldCreateFutureVisitProp = new CountryProperty();
    shouldCreateFutureVisitProp.setName(CountryPropertyConstants.SHOULD_CREATE_FUTURE_VISIT_PROP_NAME);
    shouldCreateFutureVisitProp.setValue("false");

    when(countryPropertyService.getCountryProperty(null, shouldCreateFutureVisitProp.getName()))
        .thenReturn(Optional.of(shouldCreateFutureVisitProp));

    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
    when(configService.getVaccinationProgram(visit.getPatient()))
        .thenReturn(vaccinations[0].getName());
    when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME))
        .thenReturn(true);

    when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY))
        .thenReturn(Constant.VISIT_STATUS_OCCURRED);

    when(visitService.getVisitsByPatient(patient)).thenReturn(VisitHelper.getVisits(visit));
    when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());
    when(visitService.getVisitByUuid(visit.getUuid())).thenReturn(visit);

    when(patientService.getPatient(person.getPersonId())).thenReturn(patient);

    when(locationService.getLocationAttributeTypeByName(
            CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME))
        .thenReturn(new LocationAttributeType());

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(visit.getUuid());

    // When
    updatingVisitListener.performAction(message);
    // Then
    verify(configService).isVaccinationInfoIsEnabled();
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(visitService).getVisitByUuid(visit.getUuid());
    verify(visitService).getVisitsByPatient(patient);
    verify(administrationService).getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY);
    verify(configService).getRandomizationGlobalProperty();
    verify(configService).getVaccinationProgram(visit.getPatient());
    verify(visitService).getAllVisitAttributeTypes();
  }

  private Randomization createRandomization() {
    return new Randomization(vaccinations);
  }
}
