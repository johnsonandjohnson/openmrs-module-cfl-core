package org.openmrs.module.cfl.handler;

import org.openmrs.Patient;
import org.openmrs.module.cfl.api.contract.CountrySetting;

/**
 * The WelcomeMessageSender Class.
 * <p>
 * The implementation of the WelcomeMessageSender sends Welcome Message to single Patient. The channel used to communicate
 * and exact details depend on the implementation.
 * </p>
 * <p>
 * All implementations must be Spring beans.
 * </p>
 *
 * @see org.openmrs.module.cfl.api.service.impl.WelcomeServiceImpl
 */
public interface WelcomeMessageSender {
    void send(Patient patient, CountrySetting settings);
}
