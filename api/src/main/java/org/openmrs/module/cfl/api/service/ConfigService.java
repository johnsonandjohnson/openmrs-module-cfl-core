/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.PatientFilterConfiguration;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.strategy.FindPersonFilterStrategy;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Provides the module configuration set
 */
public interface ConfigService {

    /**
     * Provides the coma separated list of relationship types ({@link
     * org.openmrs.module.cfl.api.copied.messages.model.ActorType}) used to control the list of possible targets of
     * message (not including the patient as a target).
     *
     * @return coma separated list of relationship types
     */
    String getActorTypesConfiguration();

    /**
     * Provides the default value of actor relationship direction
     *
     * @return - the default value of direction
     */
    String getDefaultActorRelationDirection();

    /**
     * Provides filter strategy {@link FindPersonFilterStrategy} for find person controller
     *
     * @return - the determined strategy or null if not defined
     */
    FindPersonFilterStrategy getPersonFilterStrategy();

    /**
     * Provides the max number of people to store as last viewed for a single user.
     *
     * @return - the configured last viewed people number limit or the default value if not defined
     */
    int getLastViewedPersonSizeLimit();

    Randomization getRandomizationGlobalProperty();

    String getVaccinationProgram(Person person);

    boolean isVaccinationInfoIsEnabled();

    String getDefaultUserTimeZone();

    Map<String, CountrySetting> getCountrySettingMap(String globalPropertyName);

    /**
     * Gets the safe delivery time from {@code requestedDeliveryTime}.
     * <p>
     * If the {@code requestedDeliveryTime} fits the allowed time window specified by country settings then the {@code
     * requestedDeliveryTime} is returned. <br />
     * If the {@code requestedDeliveryTime} does NOT fit the allowed time window specified by country settings then the
     * next day's best contact time of the {@code patient} is returned.
     * </p>
     * <p>
     * The method allows to ensure that no communication with Patient happens outside allowed time window (e.g.: noe
     * messages delivered during teh night).
     * </p>
     *
     * @param patient               the patient to calculate delivery time for, not null
     * @param requestedDeliveryTime the requested delivery time, not null
     * @param countrySetting        the Country Settings to use to check for allowed time window, not null
     * @return the safe delivery time as described above, never null
     */
    Date getSafeMessageDeliveryDate(Patient patient, Date requestedDeliveryTime, CountrySetting countrySetting);

    List<PatientFilterConfiguration> getAdHocMessagePatientFilterConfigurations();
}
