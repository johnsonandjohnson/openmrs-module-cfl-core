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

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.contract.Randomization;
import org.openmrs.module.cflcore.api.contract.Vaccination;
import org.openmrs.module.cflcore.api.contract.VisitInformation;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.service.PatientVisitConfigService;
import org.openmrs.module.cflcore.api.service.VisitReminderService;
import org.openmrs.module.cflcore.api.service.WelcomeService;
import org.openmrs.module.cflcore.api.util.DateUtil;
import org.openmrs.module.cflcore.api.util.GlobalPropertyUtils;
import org.openmrs.module.cflcore.api.util.VisitUtil;
import org.openmrs.module.messages.api.constants.MessagesConstants;
import org.openmrs.module.messages.api.model.PatientTemplate;
import org.openmrs.module.messages.api.service.DefaultPatientTemplateService;
import org.openmrs.module.messages.api.service.TemplateFieldValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.Collections;
import java.util.List;

public class RegisteringPeopleListener extends PeopleActionListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegisteringPeopleListener.class);

  private WelcomeService welcomeService;
  private VisitReminderService visitReminderService;
  private ConfigService configService;

  @Override
  public List<String> subscribeToActions() {
    return Collections.singletonList(Event.Action.CREATED.name());
  }

  @Override
  public void performAction(Message message) {
    final Person person = extractPerson(message);

    safeCreatePatientTemplatesIfNeeded(person);
    safeSendWelcomeMessages(person);

    if (configService.isVaccinationInfoIsEnabled()) {
      createFirstVisit(person, configService.getVaccinationProgram(person));
      visitReminderService.create(person);
    }
  }

  public void setWelcomeService(WelcomeService welcomeService) {
    this.welcomeService = welcomeService;
  }

  public void setVisitReminderService(VisitReminderService visitReminderService) {
    this.visitReminderService = visitReminderService;
  }

  public void setConfigService(ConfigService configService) {
    this.configService = configService;
  }

  private void safeSendWelcomeMessages(Person person) {
    try {
      welcomeService.sendWelcomeMessages(person);
    } catch (Exception e) {
      LOGGER.error(
          "Failed to send Welcome Message after creation of the Person with UUID: "
              + person.getUuid(),
          e);
    }
  }

  private void createFirstVisit(Person person, String vaccinationProgram) {
    PatientVisitConfigService patientVisitConfigService =
        Context.getService(PatientVisitConfigService.class);
    final Patient patient = Context.getPatientService().getPatientByUuid(person.getUuid());
    if (patientVisitConfigService.shouldCreateFirstVisit(patient)) {
      final VisitInformation firstVisitInfo = getFirstVisitInfo(vaccinationProgram);
      final PersonAttribute locationAttribute =
          patient.getAttribute(CFLConstants.PERSON_LOCATION_ATTRIBUTE_DEFAULT_VALUE);

      final Visit firstVisit =
          VisitUtil.createVisitResource(patient, DateUtil.now(), firstVisitInfo);

      if (locationAttribute != null) {
        firstVisit.setLocation(
            Context.getLocationService().getLocationByUuid(locationAttribute.getValue()));
      } else {
        firstVisit.setLocation(patient.getPatientIdentifier().getLocation());
      }

      Context.getVisitService().saveVisit(firstVisit);
    }
  }

  private VisitInformation getFirstVisitInfo(String vaccinationProgram) {
    Randomization randomization = configService.getRandomizationGlobalProperty();
    Vaccination vaccination = randomization.findByVaccinationProgram(vaccinationProgram);
    if (vaccination != null) {
      return vaccination.getVisits().get(0);
    } else {
      throw new IllegalArgumentException(
          String.format("Vaccination for %s program not found", vaccinationProgram));
    }
  }

  private void safeCreatePatientTemplatesIfNeeded(Person person) {
    if (!person.getIsPatient()) {
      return;
    }

    try {
      createPatientTemplatesIfNeeded(Context.getPatientService().getPatient(person.getId()));
    } catch (Exception e) {
      LOGGER.error(
          "Failed to handle Patient Template creation for Person with UUID: " + person.getUuid(),
          e);
    }
  }

  /**
   * Creates patient templates with channel type set in {@link
   * org.openmrs.module.cflcore.CFLConstants#CREATION_PATIENT_TEMPLATES_AFTER_REGISTRATION_GP_KEY}
   * global property. If such GP does not exist or is empty or has false value, then patient
   * templates will not be created.
   *
   * @param patient patient for whom the patient templates will be created
   */
  private void createPatientTemplatesIfNeeded(Patient patient) {
    String gp =
        GlobalPropertyUtils.getGlobalProperty(
            CFLConstants.CREATION_PATIENT_TEMPLATES_AFTER_REGISTRATION_GP_KEY);

    if (isPatientTemplatesShouldNotBeCreated(gp)) {
      return;
    }

    List<PatientTemplate> patientTemplates =
        getDefaultPatientTemplateService().generateDefaultPatientTemplates(patient);
    TemplateFieldValueService templateFieldValueService =
        Context.getService(TemplateFieldValueService.class);
    patientTemplates.forEach(
        patientTemplate ->
            templateFieldValueService.updateTemplateFieldValue(
                patientTemplate.getId(), MessagesConstants.CHANNEL_TYPE_PARAM_NAME, gp));
  }

  private boolean isPatientTemplatesShouldNotBeCreated(String gpKey) {
    return StringUtils.isBlank(gpKey);
  }

  private DefaultPatientTemplateService getDefaultPatientTemplateService() {
    return Context.getRegisteredComponent(
        "messages.defaultPatientTemplateService", DefaultPatientTemplateService.class);
  }
}
