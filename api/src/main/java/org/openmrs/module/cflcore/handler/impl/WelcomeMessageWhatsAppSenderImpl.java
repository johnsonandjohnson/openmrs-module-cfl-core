package org.openmrs.module.cflcore.handler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.messages.api.constants.CountryPropertyConstants;
import org.openmrs.module.messages.api.event.SmsEventParamConstants;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.service.CountryPropertyService;

public class WelcomeMessageWhatsAppSenderImpl extends BaseWelcomeMessageSenderImpl {

  private static final Log LOGGER = LogFactory.getLog(WelcomeMessageWhatsAppSenderImpl.class);

  public WelcomeMessageWhatsAppSenderImpl() {
    super(CFLConstants.WHATSAPP_CHANNEL_TYPE);
  }

  @Override
  protected ScheduledExecutionContext decorateScheduledExecutionContext(
      ScheduledExecutionContext scheduledExecutionContext) {
    Person person = Context.getPersonService().getPerson(scheduledExecutionContext.getActorId());
    String whatsAppConfig =
        Context.getService(CountryPropertyService.class)
            .getCountryPropertyValueByPerson(
                person, CountryPropertyConstants.WHATSAPP_CONFIG_PROP_NAME)
            .orElse(null);

    scheduledExecutionContext
        .getChannelConfiguration()
        .put(SmsEventParamConstants.CONFIG_KEY, whatsAppConfig);

    return scheduledExecutionContext;
  }

  @Override
  protected boolean isSendOnPatientRegistrationEnabled(Person person) {
    String isSendWhatsAppOnPatientRegistrationPropertyValue =
        Context.getService(CountryPropertyService.class)
            .getCountryPropertyValueByPerson(
                person, CountryPropertyConstants.SEND_WHATSAPP_ON_PATIENT_REGISTRATION_PROP_NAME)
            .orElse(Boolean.FALSE.toString());
    final boolean enabled = Boolean.parseBoolean(isSendWhatsAppOnPatientRegistrationPropertyValue);

    if (!enabled) {
      LOGGER.info(
          "Welcome Message via WhatsApp has been disabled. It can be enabled via Manage Notification"
              + "Configuration page or directly in database (messages_country_property table)");
    }

    return enabled;
  }
}
