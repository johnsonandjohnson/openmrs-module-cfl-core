/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.converter;

import org.openmrs.module.cfl.api.dto.AdHocMessagePatientFilterDTO;
import org.openmrs.module.messages.domain.criteria.Condition;

import java.util.List;
import java.util.Map;

/**
 * The AdHocMessagePatientFilterConverter Class.
 * <p>
 * The converters are responsible for converting client data provided by in
 * {@link AdHocMessagePatientFilterDTO} to a collection of {@link Condition}s.
 * </p>
 * <p>
 * The concrete instances are configured to be used at Global Parameter:
 * {@link org.openmrs.module.cfl.CFLConstants#AD_HOC_MESSAGE_PATIENT_FILTERS_CONFIGURATION_GP_KEY}.
 * </p>
 */
public interface PatientFilterConverter {
    /**
     * @param properties the configuration properties, not null
     * @throws IllegalArgumentException if the {@code properties} are missing required values
     */
    void initFilter(Map<String, Object> properties);

    /**
     * @param dto the DTO to convert to Conditions, not null
     * @return the list of Conditions which represent the filtering defined by {@code dto}
     * @throws IllegalStateException if this converter object was not initialed
     */
    @SuppressWarnings("java:S1452")
    List<Condition<?, ?>> convert(AdHocMessagePatientFilterDTO dto);
}
