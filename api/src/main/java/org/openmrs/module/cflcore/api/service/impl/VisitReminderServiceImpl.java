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

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.service.CustomAdministrationService;
import org.openmrs.module.cflcore.api.service.VisitReminderService;
import org.openmrs.module.cflcore.api.util.GlobalPropertiesConstants;
import org.openmrs.module.cflcore.api.util.PersonUtil;
import org.openmrs.module.messages.api.constants.ConfigConstants;
import org.openmrs.module.messages.api.constants.MessagesConstants;
import org.openmrs.module.messages.api.service.PatientTemplateService;
import org.openmrs.module.messages.api.util.BestContactTimeHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.TimeZone;

public class VisitReminderServiceImpl implements VisitReminderService {

  private static final String PATIENT_TEMPLATE_SERVICE_BEAN_NAME =
      "messages.patientTemplateService";
  private static final String HOUR_MINUTES_SEPARATOR = ":";

  @Override
  public void create(Person person) {
    String channelTypes = getPatientChannelTypes((Patient) person);
    if (StringUtils.isNotBlank(PersonUtil.getPhoneNumber(person))
        && StringUtils.isNotBlank(channelTypes)) {
      createVisitReminder(channelTypes, person.getUuid());
    }
  }

  private String getPatientChannelTypes(Patient patient) {
    StringJoiner channelJoiner = new StringJoiner(",");
    if (getGPBooleanValue(GlobalPropertiesConstants.SHOULD_SEND_REMINDER_VIA_SMS_GP_KEY, patient)) {
      channelJoiner.add(MessagesConstants.SMS_CHANNEL_TYPE);
    }

    if (getGPBooleanValue(
        GlobalPropertiesConstants.SHOULD_SEND_REMINDER_VIA_CALL_GP_KEY, patient)) {
      channelJoiner.add(MessagesConstants.CALL_CHANNEL_TYPE);
    }

    if (getGPBooleanValue(
        GlobalPropertiesConstants.SHOULD_SEND_REMINDER_VIA_WHATSAPP_GP_KEY, patient)) {
      channelJoiner.add(MessagesConstants.WHATSAPP_CHANNEL_TYPE);
    }

    return channelJoiner.toString();
  }

  private boolean getGPBooleanValue(String gpKey, Patient patient) {
    String gpValue =
        Context.getService(CustomAdministrationService.class).getGlobalProperty(gpKey, patient);
    return Boolean.parseBoolean(gpValue);
  }

  private void createVisitReminder(String channel, String patientUuid) {
    saveBestContactTimeForPatient(patientUuid);
    Context.getRegisteredComponent(PATIENT_TEMPLATE_SERVICE_BEAN_NAME, PatientTemplateService.class)
        .createVisitReminder(channel, patientUuid);
  }

  private void saveBestContactTimeForPatient(String patientUuid) {
    Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

    if (isPatientAlreadyHasBestContactTime(patient)) {
      return;
    }

    if (patient != null && patient.getPatientIdentifier() != null) {
      Location patientLocation = patient.getPatientIdentifier().getLocation();
      if (patientLocation != null) {
        String patientTimeZone = getTimeZoneFromLocation(patientLocation);

        PersonAttribute bestContactTimeAttribute = new PersonAttribute();
        bestContactTimeAttribute.setAttributeType(getBestContactTimeAttrType());
        bestContactTimeAttribute.setValue(
            getBestContactTimeInProperFormat(patientTimeZone, patient));
        patient.addAttribute(bestContactTimeAttribute);

        Context.getPatientService().savePatient(patient);
      }
    }
  }

  private boolean isPatientAlreadyHasBestContactTime(Patient patient) {
    String patientBestContactTime = BestContactTimeHelper.getBestContactTime(patient, null);
    return StringUtils.isNotBlank(patientBestContactTime);
  }

  private String getBestContactTimeInProperFormat(String timeZone, Person person) {
    String defaultBestContactTime = BestContactTimeHelper.getBestContactTime(person, null);
    String[] splittedDefaultBestContactTime = defaultBestContactTime.split(HOUR_MINUTES_SEPARATOR);

    if (StringUtils.isBlank(timeZone)) {
      return "";
    } else {
      Calendar localTime = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
      localTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splittedDefaultBestContactTime[0]));
      localTime.set(Calendar.MINUTE, Integer.parseInt(splittedDefaultBestContactTime[1]));
      SimpleDateFormat sdf = new SimpleDateFormat(CFLConstants.BEST_CONTACT_TIME_FORMAT);
      sdf.setCalendar(localTime);

      String personTimeZone =
          Context.getService(CustomAdministrationService.class)
              .getGlobalProperty(CFLConstants.DEFAULT_USER_TIME_ZONE_GP_NAME, person);
      Calendar defaultTime = Calendar.getInstance(TimeZone.getTimeZone(personTimeZone));
      defaultTime.setTime(localTime.getTime());
      sdf.setTimeZone(TimeZone.getTimeZone(personTimeZone));

      return sdf.format(defaultTime.getTime());
    }
  }

  private String getTimeZoneFromLocation(Location location) {
    String patientTimeZone = "";
    Collection<LocationAttribute> locationAttributes = location.getActiveAttributes();
    for (LocationAttribute locationAttribute : locationAttributes) {
      if (locationAttribute.getAttributeType().equals(getPatientTimeZoneAttrType())) {
        patientTimeZone = locationAttribute.getValueReference();
        break;
      }
    }
    return patientTimeZone;
  }

  private LocationAttributeType getPatientTimeZoneAttrType() {
    return Context.getLocationService()
        .getLocationAttributeTypeByName(CFLConstants.PATIENT_TIMEZONE_LOCATION_ATTR_TYPE_NAME);
  }

  private PersonAttributeType getBestContactTimeAttrType() {
    return Context.getPersonService()
        .getPersonAttributeTypeByUuid(ConfigConstants.PERSON_CONTACT_TIME_TYPE_UUID);
  }
}
