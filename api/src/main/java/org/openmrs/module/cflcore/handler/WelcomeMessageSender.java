/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.handler;

import org.openmrs.Patient;
import org.openmrs.module.cflcore.api.contract.CountrySetting;

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
 * @see org.openmrs.module.cflcore.api.service.impl.WelcomeServiceImpl
 */
public interface WelcomeMessageSender {
    void send(Patient patient, CountrySetting settings);
}
