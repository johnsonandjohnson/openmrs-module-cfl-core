/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.util;

public final class GlobalPropertiesConstants {

  public static final String PATIENT_UUID_PARAM = "patientId";
  public static final String VISIT_UUID_PARAM = "visitId";
  public static final String ENCOUNTER_UUID_PARAM = "encounterId";

  public static final GPDefinition VISIT_FORM_URIS =
      new GPDefinition(
          "visits.visit-form-uris",
          "",
          String.format(
              "The URI templates which leads to current visit form. The value of this property is a JSON "
                  + "map. The map allows to specify URIs based on the visit type. The key in the map could be "
                  + "visitTypeUuid or visitTypeName or 'default'. The value is a nested JSON map which could "
                  + "consists of 2 entries - 'create' and 'edit' URI templates. The template could consists of "
                  + "{{%s}},{{%s}} and {{%s}} variables which will be replaced if URLs are used. \n "
                  + "Note: \n A) if invalid URI is set, no form will be used\n "
                  + "B) if URI is no specified, the form from 'default' settings will be used\n "
                  + "C) if specific and default URIs are not defined, no form will be used\n",
              PATIENT_UUID_PARAM, ENCOUNTER_UUID_PARAM, VISIT_UUID_PARAM),
          true);

  public static final GPDefinition TELEPHONE_NUMBER_PERSON_ATTRIBUTE_TYPE_UUID =
      new GPDefinition(
          "cfl.person.attributeType.telephoneNumberUuid",
          "",
          "The UUID of person attribute type for a telephone number");

  public static final GPDefinition EMAIL_ADDRESS_PERSON_ATTRIBUTE_TYPE_UUID =
      new GPDefinition(
          "cfl.person.attributeType.emailAddressUuid",
          "",
          "The UUID of person attribute type for an email address");

  public static final GPDefinition AD_HOC_SMS_MESSAGE_TEMPLATE =
      new GPDefinition(
          "cfl.notificationTemplate.adhoc.sms",
          "{ \"message\": \"##message##\" }",
          "The Velocity template for Ad Hoc SMS message. The optional placeholder ##message## is replaced by a "
              + "JSON-escaped message text provided by Ad Hoc UI user. The template is evaluated with the usual message "
              + "notification context.");

  public static final GPDefinition AD_HOC_WHATS_APP_MESSAGE_TEMPLATE =
      new GPDefinition(
          "cfl.notificationTemplate.adhoc.whatsapp",
          "{ \"message\": \"##message##\" }",
          "The Velocity template for Ad Hoc WhatsApp message. The optional placeholder ##message## is replaced by a "
              + "JSON-escaped message text provided by Ad Hoc UI user. The template is evaluated with the usual message "
              + "notification context.");

  private GlobalPropertiesConstants() {}
}
