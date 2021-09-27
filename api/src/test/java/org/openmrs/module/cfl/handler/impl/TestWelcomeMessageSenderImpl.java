package org.openmrs.module.cfl.handler.impl;

import org.openmrs.module.cfl.api.contract.CountrySetting;

public class TestWelcomeMessageSenderImpl extends BaseWelcomeMessageSenderImpl {
    private final boolean enabled;

    public TestWelcomeMessageSenderImpl(final String channelType, final boolean enabled) {
        super(channelType);
        this.enabled = enabled;
    }

    @Override
    protected boolean isSendOnPatientRegistrationEnabled(CountrySetting settings) {
        return enabled;
    }
}
