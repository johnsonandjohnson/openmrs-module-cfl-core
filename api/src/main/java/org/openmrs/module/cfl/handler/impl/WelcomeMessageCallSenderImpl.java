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
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.service.impl.CallFlowServiceResultsHandlerServiceImpl;

import java.util.Map;

/**
 * The bean is configured in moduleApplicationContext.xml.
 */
public class WelcomeMessageCallSenderImpl extends BaseWelcomeMessageSenderImpl {
    public static final String NAME = "cfl.welcomeMessageCallSender";

    private static final Log LOGGER = LogFactory.getLog(WelcomeMessageCallSenderImpl.class);

    public WelcomeMessageCallSenderImpl() {
        super(CFLConstants.CALL_CHANNEL_TYPE);
    }

    @Override
    protected ScheduledExecutionContext decorateScheduledExecutionContext(
            ScheduledExecutionContext scheduledExecutionContext, CountrySetting setting) {
        Map<String, String> channelConfiguration = scheduledExecutionContext.getChannelConfiguration();
        channelConfiguration.put(CallFlowServiceResultsHandlerServiceImpl.CALL_CHANNEL_CONF_FLOW_NAME, getCallFlowName());
        channelConfiguration.put(CallFlowServiceResultsHandlerServiceImpl.CALL_CHANNEL_CONFIG_NAME, setting.getCall());

        return scheduledExecutionContext;
    }

    @Override
    protected boolean isSendOnPatientRegistrationEnabled(CountrySetting settings) {
        final boolean enabled = settings.isPerformCallOnPatientRegistration();

        if (!enabled) {
            LOGGER.info("Welcome Message via Call has been disabled. It can be enabled in configuration in Global " +
                    "Property 'cfl.countrySettingsMap', configuration property 'performCallOnPatientRegistration'");
        }

        return enabled;
    }

    private String getCallFlowName() {
        return getAdministrationService().getGlobalProperty(CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY);
    }

    private AdministrationService getAdministrationService() {
        return Context.getAdministrationService();
    }
}
