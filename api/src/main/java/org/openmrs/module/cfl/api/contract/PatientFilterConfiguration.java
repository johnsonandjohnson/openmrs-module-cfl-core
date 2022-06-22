/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.contract;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.openmrs.module.cfl.api.converter.PatientFilterConverter;
import org.openmrs.module.cfl.api.converter.PatientFilterConverterFactory;

import java.util.Map;

public class PatientFilterConfiguration {
    private String label;
    private InputType inputType;
    private PatientFilterConverter filterConverter;
    private Map<String, Object> config;

    public PatientFilterConfiguration() {

    }

    public PatientFilterConfiguration(String label, InputType inputType, PatientFilterConverter filterConverter,
                                      Map<String, Object> config) {
        this.label = label;
        this.inputType = inputType;
        this.filterConverter = filterConverter;
        this.config = config;
    }

    public String getLabel() {
        return label;
    }

    public InputType getInputType() {
        return inputType;
    }

    public PatientFilterConverter getFilterConverter() {
        return filterConverter;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    @JsonSetter("converter")
    public void setFilterConverterByName(String converterName) {
        this.filterConverter = PatientFilterConverterFactory.getPatientFilterConverter(converterName);
    }

    public enum InputType {
        /**
         * Filter by String. (=)
         */
        STRING,
        /**
         * Filter select Entity uuid. (=)
         */
        SELECT_ENTITY_UUID,
        /**
         * Filter select many Strings. (IN)
         */
        MULTI_SELECT_STRING,
        /**
         * Filter number by Integer and with operators.
         */
        INTEGER,
        /**
         * Filter date-time by calculated age.
         */
        AGE_RANGE;
    }
}
