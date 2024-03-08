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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.LocationAttributeType;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.Constant;
import org.openmrs.module.cflcore.api.contract.Randomization;
import org.openmrs.module.cflcore.api.contract.Vaccination;
import org.openmrs.module.cflcore.api.contract.VisitInformation;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.cflcore.api.helper.VisitHelper;
import org.openmrs.module.cflcore.api.service.VaccinationService;
import org.openmrs.module.cflcore.api.util.GlobalPropertiesConstants;
import org.openmrs.module.messages.api.model.CountryProperty;
import org.openmrs.module.messages.api.service.CountryPropertyService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
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

  private List<Visit> allVisits = new ArrayList<>();

  @Before
  public void setEncounterService() {
    when(Context.getService(CountryPropertyService.class)).thenReturn(countryPropertyService);
    when(countryPropertyService.getCountryProperty(any(Concept.class), any(String.class)))
        .thenReturn(Optional.empty());
  }

  @Test
  public void performAction_updateFromMultipleThreadsSafetyTest()
      throws JMSException, InterruptedException {
    // Given
    setupDataForFutureVisitCreation();

    final VaccinationService vaccinationServiceSpy = Mockito.spy(vaccinationService);
    when(Context.getService(VaccinationService.class)).thenReturn(vaccinationServiceSpy);
    when(visitsConfigService.getOccurredVisitStatues())
        .thenReturn(Collections.singletonList(Constant.VISIT_STATUS_OCCURRED));
    when(customAdministrationService.getGlobalProperty(
            GlobalPropertiesConstants.SHOULD_CREATE_FUTURE_VISITS_GP_KEY, patient))
        .thenReturn(Boolean.TRUE.toString());

    doAnswer(
            invocationOnMock -> {
              final Visit visit = (Visit) invocationOnMock.getArguments()[0];
              visit
                  .getActiveAttributes()
                  .iterator()
                  .next()
                  .setValueReferenceInternal(Constant.VISIT_STATUS_OCCURRED);
              return invocationOnMock.callRealMethod();
            })
        .when(vaccinationServiceSpy)
        .createFutureVisits(any(Visit.class), any(Date.class));

    // When
    Runnable processMessage = () -> updatingVisitListener.performAction(message);
    Thread performActionFirstThread = new Thread(processMessage);
    Thread performActionSecondThread = new Thread(processMessage);

    performActionFirstThread.start();
    performActionSecondThread.start();

    performActionFirstThread.join();
    performActionSecondThread.join();

    // Then
    ArgumentCaptor<Visit> saveVisitArgumentCaptor = ArgumentCaptor.forClass(Visit.class);
    verify(visitService, times(2)).saveVisit(saveVisitArgumentCaptor.capture());
    assertThat(saveVisitArgumentCaptor.getAllValues().size(), is(2));
  }

  @Test
  public void performAction_shouldCreateFutureVisits() throws JMSException {
    // Given
    setupDataForFutureVisitCreation();
    when(visitsConfigService.getOccurredVisitStatues())
        .thenReturn(Collections.singletonList(Constant.VISIT_STATUS_OCCURRED));
    when(customAdministrationService.getGlobalProperty(
            GlobalPropertiesConstants.SHOULD_CREATE_FUTURE_VISITS_GP_KEY, patient))
        .thenReturn(Boolean.TRUE.toString());

    // When
    updatingVisitListener.performAction(message);
    // Then
    verify(configService).isVaccinationInfoIsEnabled();
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(visitService).getVisitByUuid(visit.getUuid());
    verify(visitService).getVisitsByPatient(patient);
    verify(visitsConfigService).getOccurredVisitStatues();
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

  private void setupDataForFutureVisitCreation() throws JMSException {
    visit =
        VisitHelper.createVisit(
            1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_OCCURRED, visitStartDate);
    allVisits.add(visit);

    CountryProperty shouldCreateFutureVisitProp = new CountryProperty();
    shouldCreateFutureVisitProp.setName(
        GlobalPropertiesConstants.SHOULD_CREATE_FUTURE_VISITS_GP_KEY);
    shouldCreateFutureVisitProp.setValue("true");

    when(countryPropertyService.getCountryProperty(null, shouldCreateFutureVisitProp.getName()))
        .thenReturn(Optional.of(shouldCreateFutureVisitProp));
    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
    when(configService.getVaccinationProgram(visit.getPatient()))
        .thenReturn(vaccinations[0].getName());
    when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME))
        .thenReturn(true);

    when(visitService.getAllVisitTypes())
        .thenReturn(
            Arrays.stream(vaccinations)
                .map(Vaccination::getVisits)
                .flatMap(Collection::stream)
                .map(VisitInformation::getNameOfDose)
                .map(VisitHelper::createVisitType)
                .collect(Collectors.toList()));

    when(visitService.getVisitByUuid(visit.getUuid())).thenReturn(visit);
    when(visitService.saveVisit(any(Visit.class)))
        .thenAnswer(
            invocationOnMock -> {
              final Visit savedVisit = (Visit) invocationOnMock.getArguments()[0];
              allVisits.add(savedVisit);
              return savedVisit;
            });
    when(visitService.getVisitsByPatient(patient)).thenReturn(allVisits);

    when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());

    when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY))
        .thenReturn(Constant.VISIT_STATUS_OCCURRED);

    when(patientService.getPatient(person.getPersonId())).thenReturn(patient);

    when(locationService.getLocationAttributeTypeByName(
            CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME))
        .thenReturn(new LocationAttributeType());

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(visit.getUuid());
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
    when(visitsConfigService.getOccurredVisitStatues())
        .thenReturn(Arrays.asList(Constant.VISIT_STATUS_OCCURRED));
    // When
    updatingVisitListener.performAction(message);
    // Then
    verify(configService).isVaccinationInfoIsEnabled();
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(visitService).getVisitByUuid(visit.getUuid());
    verify(visitsConfigService).getOccurredVisitStatues();
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
    shouldCreateFutureVisitProp.setName(
        GlobalPropertiesConstants.SHOULD_CREATE_FUTURE_VISITS_GP_KEY);
    shouldCreateFutureVisitProp.setValue("false");

    when(countryPropertyService.getCountryProperty(null, shouldCreateFutureVisitProp.getName()))
        .thenReturn(Optional.of(shouldCreateFutureVisitProp));

    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
    when(configService.getVaccinationProgram(visit.getPatient()))
        .thenReturn(vaccinations[0].getName());
    when(configService.isVaccinationListenerEnabled(CFLConstants.VACCINATION_VISIT_LISTENER_NAME))
        .thenReturn(true);
    when(visitsConfigService.getOccurredVisitStatues())
        .thenReturn(Collections.singletonList(Constant.VISIT_STATUS_OCCURRED));

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
    verify(visitsConfigService).getOccurredVisitStatues();
    verify(configService).getRandomizationGlobalProperty();
    verify(configService).getVaccinationProgram(visit.getPatient());
    verify(visitService).getAllVisitAttributeTypes();
  }

  private Randomization createRandomization() {
    return new Randomization(vaccinations);
  }
}
