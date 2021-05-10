package org.openmrs.module.cfl.handler.impl;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.service.impl.CallFlowServiceResultsHandlerServiceImpl;

/**
 * The bean is configured in moduleApplicationContext.xml.
 */
public class WelcomeMessageCallSenderImpl extends BaseWelcomeMessageSenderImpl {
    public static final String NAME = "cfl.welcomeMessageCallSender";

    public WelcomeMessageCallSenderImpl() {
        super(CFLConstants.CALL_CHANNEL_TYPE);
    }

    @Override
    protected ScheduledExecutionContext decorateScheduledExecutionContext(
            ScheduledExecutionContext scheduledExecutionContext) {
        scheduledExecutionContext
                .getChannelConfiguration()
                .put(CallFlowServiceResultsHandlerServiceImpl.CALL_CHANNEL_CONF_FLOW_NAME, getCallFlowName());
        return scheduledExecutionContext;
    }

    @Override
    protected boolean isSendOnPatientRegistrationEnabled(CountrySetting settings) {
        return settings.isPerformCallOnPatientRegistration();
    }

    private String getCallFlowName() {
        return getAdministrationService().getGlobalProperty(CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY);
    }

    private AdministrationService getAdministrationService() {
        return Context.getAdministrationService();
    }
}
