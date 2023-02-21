package org.openmrs.module.cflcore.handler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.contract.CountrySetting;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.service.impl.AbstractTextMessageServiceResultsHandlerService;

public class WelcomeMessageWhatsAppSenderImpl extends BaseWelcomeMessageSenderImpl {

  private static final Log LOGGER = LogFactory.getLog(WelcomeMessageWhatsAppSenderImpl.class);

  public WelcomeMessageWhatsAppSenderImpl() {
    super(CFLConstants.WHATSAPP_CHANNEL_TYPE);
  }

  @Override
  protected ScheduledExecutionContext decorateScheduledExecutionContext(
    ScheduledExecutionContext scheduledExecutionContext, CountrySetting setting) {
    scheduledExecutionContext.getChannelConfiguration()
      .put(AbstractTextMessageServiceResultsHandlerService.CONFIG_KEY, setting.getWhatsApp());

    return scheduledExecutionContext;
  }

  @Override
  protected boolean isSendOnPatientRegistrationEnabled(CountrySetting settings) {
    final boolean enabled = settings.isSendWhatsAppOnPatientRegistration();

    if (!enabled) {
      LOGGER.info("Welcome Message via WhatsApp has been disabled. It can be enabled via Manage Notification"
        + "Configuration page or directly in database (messages_country_property table)");
    }

    return enabled;
  }
}
