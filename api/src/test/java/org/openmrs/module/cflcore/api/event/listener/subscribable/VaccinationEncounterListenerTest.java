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
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.LocationAttributeType;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.Constant;
import org.openmrs.module.cflcore.api.contract.Randomization;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.cflcore.api.helper.VisitHelper;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.module.cflcore.api.util.GlobalPropertiesConstants;
import org.openmrs.module.messages.api.model.CountryProperty;
import org.openmrs.module.messages.api.service.CountryPropertyService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jms.JMSException;
import java.util.Collections;
import java.util.Date;
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
public class VaccinationEncounterListenerTest extends VaccinationListenerBaseTest {
  private final Date encounterDate = new Date(1620743880000L); // 2021-05-11T14:38:00UTC
  private final Date expectedSecondVisit = new Date(1621003080000L); // 2021-05-14T14:38:00UTC
  private final Date expectedThirdVisit = new Date(1621348680000L); // 2021-05-18T14:38:00UTC

  @Mock protected EncounterService encounterService;

  @Mock private CountryPropertyService countryPropertyService;

  @InjectMocks private VaccinationEncounterListener vaccinationEncounterListener;

  private Encounter encounter;

  @Before
  public void setEncounterService() {
    when(Context.getEncounterService()).thenReturn(encounterService);
    when(Context.getService(CountryPropertyService.class)).thenReturn(countryPropertyService);
    when(countryPropertyService.getCountryProperty(any(Concept.class), any(String.class)))
        .thenReturn(Optional.empty());
  }

  @Test
  public void performAction_shouldCreateFutureVisits() throws JMSException {
    // Given
    visit =
        VisitHelper.createVisit(
            1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_OCCURRED, DateUtil.now());
    encounter = VisitHelper.createEncounter(2, encounterDate, visit);
    visit.addEncounter(encounter);

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
    when(configService.getVaccinationEncounterTypeUUIDs())
        .thenReturn(Collections.singleton(encounter.getEncounterType().getUuid()));
    when(configService.isVaccinationListenerEnabled(
            CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME))
        .thenReturn(true);

    when(encounterService.getEncounterByUuid(encounter.getUuid())).thenReturn(encounter);

    when(visitService.getVisitByUuid(Constant.VISIT_UUID)).thenReturn(visit);
    when(visitService.getVisitsByPatient(patient)).thenReturn(VisitHelper.getVisits(visit));
    when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());

    when(Context.getService(org.openmrs.module.visits.api.service.ConfigService.class)
            .getOccurredVisitStatues())
        .thenReturn(Collections.singletonList(Constant.VISIT_STATUS_OCCURRED));
    when(patientService.getPatient(person.getPersonId())).thenReturn(patient);

    when(locationService.getLocationAttributeTypeByName(
            CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME))
        .thenReturn(new LocationAttributeType());

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(encounter.getUuid());
    when(customAdministrationService.getGlobalProperty(
            GlobalPropertiesConstants.SHOULD_CREATE_FUTURE_VISITS_GP_KEY, patient))
        .thenReturn(Boolean.TRUE.toString());

    // When
    vaccinationEncounterListener.performAction(message);
    // Then
    verify(configService).isVaccinationInfoIsEnabled();
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(encounterService).getEncounterByUuid(encounter.getUuid());
    verify(configService, times(3)).getRandomizationGlobalProperty();
    verify(configService, times(3)).getVaccinationProgram(visit.getPatient());
    verify(visitService, times(16)).getAllVisitAttributeTypes();

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
    when(configService.isVaccinationListenerEnabled(
            CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME))
        .thenReturn(true);

    when(configService.isVaccinationInfoIsEnabled()).thenReturn(false);
    // When
    vaccinationEncounterListener.performAction(message);
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
    when(configService.isVaccinationListenerEnabled(
            CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME))
        .thenReturn(false);

    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    // When
    vaccinationEncounterListener.performAction(message);
    // Then
    verify(configService)
        .isVaccinationListenerEnabled(CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME);
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
    encounter = VisitHelper.createEncounter(2, encounterDate, visit);
    visit.addEncounter(encounter);

    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.getVaccinationEncounterTypeUUIDs())
        .thenReturn(Collections.singleton(encounter.getEncounterType().getUuid()));
    when(configService.isVaccinationListenerEnabled(
            CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME))
        .thenReturn(true);

    when(encounterService.getEncounterByUuid(encounter.getUuid())).thenReturn(encounter);
    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(encounter.getUuid());
    when(visitsConfigService.getOccurredVisitStatues())
        .thenReturn(Collections.singletonList(Constant.VISIT_STATUS_OCCURRED));

    // When
    vaccinationEncounterListener.performAction(message);
    // Then
    verify(configService).isVaccinationInfoIsEnabled();
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(encounterService).getEncounterByUuid(encounter.getUuid());
    verifyZeroInteractions(patientService);
    verifyZeroInteractions(locationService);
  }

  @Test
  public void performAction_shouldThrowCflRuntimeExceptionIfEncounterNotFoundForGivenVisitUuid()
      throws JMSException {
    // Given
    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.isVaccinationListenerEnabled(
            CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME))
        .thenReturn(true);
    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(Constant.VISIT_UUID);
    when(encounterService.getEncounterByUuid(anyString())).thenReturn(null);
    // When
    try {
      vaccinationEncounterListener.performAction(message);
      Assert.fail("Unable to retrieve visit by uuid: null");
    } catch (CflRuntimeException e) {
      // Then
      verify(configService).isVaccinationInfoIsEnabled();
      verify(message).getString(CFLConstants.UUID_KEY);
      verify(encounterService).getEncounterByUuid(anyString());
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
            1, patient, Constant.VISIT_TYPE_DOSING, Constant.VISIT_STATUS_OCCURRED, DateUtil.now());
    encounter = VisitHelper.createEncounter(2, encounterDate, visit);
    visit.addEncounter(encounter);

    CountryProperty shouldCreateFutureVisitProp = new CountryProperty();
    shouldCreateFutureVisitProp.setName(
        GlobalPropertiesConstants.SHOULD_CREATE_FUTURE_VISITS_GP_KEY);
    shouldCreateFutureVisitProp.setValue("false");

    when(countryPropertyService.getCountryProperty(null, shouldCreateFutureVisitProp.getName()))
        .thenReturn(Optional.of(shouldCreateFutureVisitProp));
    when(configService.isVaccinationInfoIsEnabled()).thenReturn(true);
    when(configService.getVaccinationEncounterTypeUUIDs())
        .thenReturn(Collections.singleton(encounter.getEncounterType().getUuid()));
    when(configService.getRandomizationGlobalProperty()).thenReturn(createRandomization());
    when(configService.getVaccinationProgram(visit.getPatient()))
        .thenReturn(vaccinations[0].getName());
    when(configService.isVaccinationListenerEnabled(
            CFLConstants.VACCINATION_ENCOUNTER_LISTENER_NAME))
        .thenReturn(true);

    when(encounterService.getEncounterByUuid(encounter.getUuid())).thenReturn(encounter);

    when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY))
        .thenReturn(Constant.VISIT_STATUS_OCCURRED);

    when(visitService.getVisitsByPatient(patient)).thenReturn(VisitHelper.getVisits(visit));
    when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.getVisitAttributeTypes());

    when(patientService.getPatient(person.getPersonId())).thenReturn(patient);

    when(locationService.getLocationAttributeTypeByName(
            CFLConstants.COUNTRY_LOCATION_ATTR_TYPE_NAME))
        .thenReturn(new LocationAttributeType());

    when(message.getString(CFLConstants.UUID_KEY)).thenReturn(encounter.getUuid());
    when(visitsConfigService.getOccurredVisitStatues())
        .thenReturn(Collections.singletonList(Constant.VISIT_STATUS_OCCURRED));

    // When
    vaccinationEncounterListener.performAction(message);
    // Then
    verify(configService).isVaccinationInfoIsEnabled();
    verify(message).getString(CFLConstants.UUID_KEY);
    verify(encounterService).getEncounterByUuid(encounter.getUuid());
    verify(configService).getRandomizationGlobalProperty();
    verify(configService).getVaccinationProgram(visit.getPatient());
    verify(visitService).getAllVisitAttributeTypes();
  }

  private Randomization createRandomization() {
    return new Randomization(vaccinations);
  }
}
