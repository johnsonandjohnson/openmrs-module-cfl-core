/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.event;

import org.openmrs.Person;
import org.openmrs.User;

/**
 * Public API for Application level events
 */
public interface ApplicationEventService extends org.openmrs.module.emrapi.event.ApplicationEventService {

    /**
     * Fires an application level event that the specified person has been viewed by the specified
     * user.
     *
     * @should publish the person viewed event
     */
    void personViewed(Person person, User user);
}
