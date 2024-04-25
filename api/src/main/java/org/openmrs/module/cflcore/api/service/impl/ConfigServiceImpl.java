/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.constant.ConfigConstants;
import org.openmrs.module.cflcore.api.contract.PatientFilterConfiguration;
import org.openmrs.module.cflcore.api.contract.Randomization;
import org.openmrs.module.cflcore.api.contract.Vaccination;
import org.openmrs.module.cflcore.api.copied.messages.model.RelationshipTypeDirection;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.strategy.FindPersonFilterStrategy;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.module.cflcore.api.util.GlobalPropertyUtils;
import org.openmrs.module.messages.api.constants.CountryPropertyConstants;
import org.openmrs.module.messages.api.service.CountryPropertyService;
import org.openmrs.module.messages.api.util.BestContactTimeHelper;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.openmrs.module.cflcore.CFLConstants.VACCINATION_PROGRAM_KEY;

public class ConfigServiceImpl implements ConfigService {

  private PersonService personService;

  @Override
  public String getActorTypesConfiguration() {
    return getGp(CFLConstants.ACTOR_TYPES_KEY);
  }

  @Override
  public String getDefaultActorRelationDirection() {
    return RelationshipTypeDirection.A.name();
  }

  @Override
  public FindPersonFilterStrategy getPersonFilterStrategy() {
    String beanName = getGp(ConfigConstants.FIND_PERSON_FILTER_STRATEGY_KEY);
    if (StringUtils.isBlank(beanName)) {
      return null;
    }
    return Context.getRegisteredComponent(beanName, FindPersonFilterStrategy.class);
  }

  @Override
  public int getLastViewedPersonSizeLimit() {
    String gpName = ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_KEY;
    return GlobalPropertyUtils.parseInt(
        gpName, getGp(gpName, ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DEFAULT_VALUE));
  }

  @Override
  public Randomization getRandomizationGlobalProperty() {
    return new Randomization(
        new Gson().fromJson(getGp(VACCINATION_PROGRAM_KEY), Vaccination[].class));
  }

  @Override
  public String getVaccinationProgram(Person person) {
    final PersonAttribute vaccinationProgramAttribute =
        person.getAttribute(CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME);

    return vaccinationProgramAttribute != null ? vaccinationProgramAttribute.getValue() : null;
  }

  @Override
  public boolean isVaccinationInfoIsEnabled() {
    return Boolean.parseBoolean(
        Context.getAdministrationService()
            .getGlobalProperty(CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY));
  }

  @Override
  public Set<String> getVaccinationEncounterTypeUUIDs() {
    final String rawValue =
        Context.getAdministrationService()
            .getGlobalProperty(CFLConstants.VACCINATION_VISIT_ENCOUNTER_TYPE_UUID_LIST_KEY);

    if (rawValue == null) {
      return Collections.emptySet();
    }

    return Stream.of(rawValue.split(CFLConstants.COMMA_SEPARATOR))
        .map(String::trim)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isVaccinationListenerEnabled(String listenerName) {
    final String rawValue =
        Context.getAdministrationService().getGlobalProperty(CFLConstants.VACCINATION_LISTENER_KEY);

    return listenerName.equalsIgnoreCase(rawValue.trim());
  }

  @Override
  public Date getSafeMessageDeliveryDate(Patient patient, Date requestedDeliveryTime) {
    CountryPropertyService countryPropertyService =
        Context.getService(CountryPropertyService.class);

    String timeFrom =
        countryPropertyService
            .getCountryPropertyValueByPerson(
                patient.getPerson(),
                CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_FROM_PROP_NAME)
            .orElse(CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_FROM_DEFAULT_VALUE);
    String timeTo =
        countryPropertyService
            .getCountryPropertyValueByPerson(
                patient.getPerson(),
                CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_TO_PROP_NAME)
            .orElse(CountryPropertyConstants.PATIENT_NOTIFICATION_TIME_WINDOW_TO_DEFAULT_VALUE);

    final TimeZone defaultUserTimezone = DateUtil.getDefaultUserTimezone();
    final ZonedDateTime requestedDeliveryZonedDateTime =
        ZonedDateTime.ofInstant(requestedDeliveryTime.toInstant(), defaultUserTimezone.toZoneId());
    final ZonedDateTime allowedTimeWindowFrom =
        ZonedDateTime.of(
            requestedDeliveryZonedDateTime.toLocalDate(),
            LocalTime.parse(timeFrom),
            requestedDeliveryZonedDateTime.getZone());
    final ZonedDateTime allowedTimeWindowTo =
        ZonedDateTime.of(
            requestedDeliveryZonedDateTime.toLocalDate(),
            LocalTime.parse(timeTo),
            requestedDeliveryZonedDateTime.getZone());
    final Date safeDeliveryDate;

    if (requestedDeliveryZonedDateTime.isBefore(allowedTimeWindowFrom)) {
      // Get best contact time for the same day
      safeDeliveryDate = getBestContactTimeAt(requestedDeliveryTime, patient, defaultUserTimezone);
    } else if (requestedDeliveryZonedDateTime.isAfter(allowedTimeWindowTo)) {
      // Get best contact time for the next day
      safeDeliveryDate =
          getBestContactTimeAt(
              DateUtil.addDaysToDate(requestedDeliveryTime, 1), patient, defaultUserTimezone);
    } else {
      // delivery date fits allowed time window
      safeDeliveryDate = requestedDeliveryTime;
    }

    return safeDeliveryDate;
  }

  @Override
  public List<PatientFilterConfiguration> getAdHocMessagePatientFilterConfigurations() {
    final String configurationJson =
        getGp(CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_KEY);
    final ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(
          configurationJson, new TypeReference<List<PatientFilterConfiguration>>() {});
    } catch (IOException ioe) {
      throw new APIException(
          "Malformed content of parameter: "
              + CFLConstants.AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_KEY,
          ioe);
    }
  }

  public void setPersonService(PersonService personService) {
    this.personService = personService;
  }

  private Date getBestContactTimeAt(
      final Date date, final Patient patient, final TimeZone timeZone) {
    final String bestContactTime = getBestContactTime(patient);
    return DateUtil.getDateWithTimeOfDay(date, bestContactTime, timeZone);
  }

  private String getBestContactTime(final Patient patient) {
    final RelationshipType caregiverType =
        personService.getRelationshipTypeByUuid(CFLConstants.CAREGIVER_RELATIONSHIP_UUID);
    return BestContactTimeHelper.getBestContactTime(patient, caregiverType);
  }

  private String getGp(String propertyName) {
    return Context.getAdministrationService().getGlobalProperty(propertyName);
  }

  private String getGp(String propertyName, String defaultValue) {
    return Context.getAdministrationService().getGlobalProperty(propertyName, defaultValue);
  }
}
