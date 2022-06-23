/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service;

import org.openmrs.module.cflcore.api.model.GlobalPropertyHistory;

import java.util.Optional;

/**
 * Provides methods for management of {@link org.openmrs.module.cflcore.api.model.GlobalPropertyHistory} entity.
 */
public interface GlobalPropertyHistoryService {

    /**
     * Gets the last value of particular global property. It is retrieved from global_property_history table.
     *
     * @param gpName - the name of global property (property column)
     * @return - the last string value of given global property
     */
    Optional<GlobalPropertyHistory> getPreviousValueOfGlobalProperty(String gpName);
}
