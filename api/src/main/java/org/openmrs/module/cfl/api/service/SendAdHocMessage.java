package org.openmrs.module.cfl.api.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.messages.api.event.MessagesEvent;
import org.openmrs.module.messages.api.event.SmsEventParamConstants;
import org.openmrs.module.messages.api.service.MessagesEventService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendAdHocMessage {
  private static final Log LOGGER = LogFactory.getLog(SendAdHocMessage.class);
  private static final String MESSAGES_EVENT_SERVICE_BEAN_NAME = "messages.messagesEventService";
  private static final String SMS_INITIATE_EVENT = "send_sms";
  private static final String SMS_CONFIG_KEY = "config";

  private static final String GET_PATIENT_PHONES_SQL =
      "SELECT \n"
          + "    phone_pa.value\n"
          + "FROM\n"
          + "    patient AS p\n"
          + "INNER JOIN\n"
          + "    person_attribute AS phone_pa ON phone_pa.person_id = p.patient_id\n"
          + "INNER JOIN\n"
          + "    person_attribute_type AS phone_pat ON\n"
          + "        phone_pat.person_attribute_type_id = phone_pa.person_attribute_type_id\n"
          + "        AND phone_pat.name = 'Telephone Number'\n"
          + "INNER JOIN\n"
          + "    person_attribute AS lang_pa ON lang_pa.person_id = p.patient_id\n"
          + "INNER JOIN\n"
          + "    person_attribute_type AS lang_pat ON lang_pat.person_attribute_type_id = lang_pa.person_attribute_type_id\n"
          + "        AND lang_pat.name = 'personLanguage'\n"
          + "WHERE\n"
          + "    p.voided = 0\n"
          + "    AND NOT NULLIF(phone_pa.value,'') IS NULL\n"
          + "    AND lang_pa.value = :language";

  private DbSessionFactory dbSessionFactory;

  public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  public void sendSMS(String templateName, String language, String smsConfigName) {
    LOGGER.info("SendAdHocMessage.sendSMS started.");

    try {
      final List<String> patientPhones = getPatientPhones(language);
      final SMSContext context = new SMSContext(templateName, language, smsConfigName);

      sendSMS(patientPhones, context);
    } catch (Exception any) {
      LOGGER.error("FAILED TO SEND SMS! Caused: " + any.toString(), any);
    }

    LOGGER.info("SendAdHocMessage.sendSMS ended.");
  }

  private List<String> getPatientPhones(String language) {
    LOGGER.info("Reading Patient phones of language: " + language);
    final List<String> phones =
        dbSessionFactory
            .getCurrentSession()
            .createSQLQuery(GET_PATIENT_PHONES_SQL)
            .setParameter("language", language)
            .list();

    LOGGER.info("Read Patient phones: " + phones.size());
    return phones;
  }

  private void sendSMS(List<String> patientPhones, SMSContext context) {
    LOGGER.info("Sending SMS to " + patientPhones.size() + "patients.");

    for (String patientPhone : patientPhones) {
      sendSMS(patientPhone, context);
    }
  }

  private void sendSMS(String patientPhone, SMSContext context) {
    final Map<String, String> customParameters = new HashMap<String, String>();
    customParameters.put("language", context.language);
    customParameters.put("parameters", "");

    final Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(
        SmsEventParamConstants.RECIPIENTS,
        new ArrayList<String>(Collections.singletonList(patientPhone)));
    properties.put(SmsEventParamConstants.MESSAGE, context.templateName);
    properties.put(SmsEventParamConstants.CUSTOM_PARAMS, customParameters);
    properties.put(SMS_CONFIG_KEY, context.smsConfigName);

    Context.getRegisteredComponent(MESSAGES_EVENT_SERVICE_BEAN_NAME, MessagesEventService.class)
        .sendEventMessage(new MessagesEvent(SMS_INITIATE_EVENT, properties));
  }

  static class SMSContext {
    private final String templateName;
    private final String language;
    private final String smsConfigName;

    SMSContext(String templateName, String language, String smsConfigName) {
      this.templateName = templateName;
      this.language = language;
      this.smsConfigName = smsConfigName;
    }

  }
}
