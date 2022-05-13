/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
