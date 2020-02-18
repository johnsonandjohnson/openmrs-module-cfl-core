/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.copied.messages.service;


/**
 * Provides the module configuration set
 */
public interface ConfigService {

    /**
     * Provides the coma separated list of relationship types ({@link org.openmrs.module.messages.api.model.ActorType})
     * used to control the list of possible targets of message (not including the patient as a target).
     * @return coma separated list of relationship types
     */
    String getActorTypesConfiguration();

    /**
     * Provides the default value of actor relationship direction
     * @return - the default value of direction
     */
    String getDefaultActorRelationDirection();
}
