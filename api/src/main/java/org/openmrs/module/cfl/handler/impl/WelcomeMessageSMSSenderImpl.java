package org.openmrs.module.cfl.handler.impl;

import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;

/**
 * The bean is configured in moduleApplicationContext.xml.
 */
public class WelcomeMessageSMSSenderImpl extends BaseWelcomeMessageSenderImpl {
    public static final String NAME = "cfl.welcomeMessageSMSSender";

    public WelcomeMessageSMSSenderImpl() {
        super(CFLConstants.SMS_CHANNEL_TYPE);
    }

    @Override
    protected boolean isSendOnPatientRegistrationEnabled(CountrySetting settings) {
        return settings.isSendSmsOnPatientRegistration();
    }
}
