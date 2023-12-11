/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.patienheader;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.patienheader.model.PatientHeaderFieldModel;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.StringJoiner;

public class PatientHeaderValueAccessor {

  private static final Log LOGGER = LogFactory.getLog(PatientHeaderValueAccessor.class);

  private final Patient patient;

  private final List<PatientIdentifierType> patientIdentifierTypes;

  private final List<PersonAttributeType> personAttributeTypes;

  public PatientHeaderValueAccessor(Patient patient) {
    this.patient = patient;
    this.patientIdentifierTypes = Context.getPatientService().getAllPatientIdentifierTypes(false);
    this.personAttributeTypes = Context.getPersonService().getAllPersonAttributeTypes(false);
  }

  public String getValue(PatientHeaderFieldModel fieldModel) {
    StringJoiner joiner = new StringJoiner("");
    if (StringUtils.isNotBlank(fieldModel.getName())) {
      handlePatientIdentifier(fieldModel, joiner);
      handlePersonAttribute(fieldModel, joiner);
      handleOtherPatientField(fieldModel, joiner);
    } else if ("static".equalsIgnoreCase(fieldModel.getType())) {
      joiner.add(fieldModel.getValue());
    }

    return joiner.toString();
  }

  private void handlePatientIdentifier(PatientHeaderFieldModel field, StringJoiner joiner) {
    for (PatientIdentifierType patientIdentifierType : patientIdentifierTypes) {
      if (!field.getName().equalsIgnoreCase(patientIdentifierType.getName())) {
        continue;
      }

      PatientIdentifier patientIdentifier = patient.getPatientIdentifier(field.getName());
      joiner.add(patientIdentifier != null ? patientIdentifier.getIdentifier() : "");
    }
  }

  private void handlePersonAttribute(PatientHeaderFieldModel field, StringJoiner joiner) {
    for (PersonAttributeType personAttributeType : personAttributeTypes) {
      if (!field.getName().equalsIgnoreCase(personAttributeType.getName())) {
        continue;
      }

      PersonAttribute personAttribute = patient.getAttribute(field.getName());
      if (personAttribute != null) {
        joiner.add(getValueFromPersonAttribute(field.getType(), personAttribute));
      } else {
        joiner.add(StringUtils.EMPTY);
      }
    }
  }

  private String getValueFromPersonAttribute(String fieldType, PersonAttribute personAttribute) {
    if ("location".equalsIgnoreCase(fieldType)) {
      Location location =
          Context.getLocationService().getLocationByUuid(personAttribute.getValue());
      return location != null ? location.getName() : StringUtils.EMPTY;
    } else {
      return personAttribute.getValue();
    }
  }

  private void handleOtherPatientField(PatientHeaderFieldModel field, StringJoiner joiner) {
    try {
      Object value = PropertyUtils.getProperty(patient, field.getName());
      joiner.add(value != null ? value.toString() : StringUtils.EMPTY);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      LOGGER.error(String.format("Property %s not found", field.getName()));
      joiner.add(StringUtils.EMPTY);
    }
  }
}
