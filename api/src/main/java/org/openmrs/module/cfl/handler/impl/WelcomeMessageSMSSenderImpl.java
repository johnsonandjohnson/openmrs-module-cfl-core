package org.openmrs.module.cfl.handler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.service.impl.SmsServiceResultsHandlerServiceImpl;

/**
 * The bean is configured in moduleApplicationContext.xml.
 */
public class WelcomeMessageSMSSenderImpl extends BaseWelcomeMessageSenderImpl {
    public static final String NAME = "cfl.welcomeMessageSMSSender";

    private static final Log LOGGER = LogFactory.getLog(WelcomeMessageSMSSenderImpl.class);

    public WelcomeMessageSMSSenderImpl() {
        super(CFLConstants.SMS_CHANNEL_TYPE);
    }

    @Override
    protected ScheduledExecutionContext decorateScheduledExecutionContext(
            ScheduledExecutionContext scheduledExecutionContext, CountrySetting setting) {
        scheduledExecutionContext.getChannelConfiguration()
                .put(SmsServiceResultsHandlerServiceImpl.SMS_CHANNEL_CONFIG_NAME, setting.getSms());

        return scheduledExecutionContext;
    }

    @Override
    protected boolean isSendOnPatientRegistrationEnabled(CountrySetting settings) {
        final boolean enabled = settings.isSendSmsOnPatientRegistration();

        if (!enabled) {
            LOGGER.info("Welcome Message via SMS has been disabled. It can be enabled in configuration in Global " +
                    "Property 'cfl.countrySettingsMap', configuration property 'sendSmsOnPatientRegistration'");
        }

        return enabled;
    }
}
