/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.service;

import org.openmrs.Person;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.strategy.FindPersonFilterStrategy;

/**
 * Provides the module configuration set
 */
public interface ConfigService {

    /**
     * Provides the coma separated list of relationship types ({@link
     * org.openmrs.module.cfl.api.copied.messages.model.ActorType}) used to control the list of possible targets of
     * message (not including the patient as a target).
     * @return coma separated list of relationship types
     */
    String getActorTypesConfiguration();

    /**
     * Provides the default value of actor relationship direction
     * @return - the default value of direction
     */
    String getDefaultActorRelationDirection();

    /**
     * Provides filter strategy {@link FindPersonFilterStrategy} for find person controller
     * @return - the determined strategy or null if not defined
     */
    FindPersonFilterStrategy getPersonFilterStrategy();

    /**
     * Provides the max number of people to store as last viewed for a single user.
     * @return - the configured last viewed people number limit or the default value if not defined
     */
    int getLastViewedPersonSizeLimit();

    Vaccination[] getRandomizationGlobalProperty();

    String getVaccinationProgram(Person person);

    boolean isVaccinationInfoIsEnabled();
}
