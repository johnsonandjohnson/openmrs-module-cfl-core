/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.service;

import org.openmrs.PersonAttribute;
import org.openmrs.event.Event;

/**
 * The PersonAttributeListenerService Class.
 * <p>
 * The Person Attribute Listener service is responsible for logic executed when PersonAttribute is created, updated or
 * deleted.
 * </p>
 */
public interface PersonAttributeListenerService {
    /**
     * Performs 'on attribute changed' logic.
     *
     * @param action          the type of action which caused the change of attribute (CREATED, UPDATED, VOIDED), not null
     * @param personAttribute the attribute which has change (new state), not null
     */
    void onPersonAttributeEvent(Event.Action action, PersonAttribute personAttribute);
}
