package org.openmrs.module.cfl.api.contract;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.openmrs.module.cfl.api.converter.PatientFilterConverter;

import java.util.Map;

public class PatientFilterConfiguration {
    private static final String CONVERTER_PACKAGE_PREFIX = "org.openmrs.module.cfl.api.converter.";

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
    public void setFilterConverterByName(String converterName)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.filterConverter =
                (PatientFilterConverter) Class.forName(CONVERTER_PACKAGE_PREFIX + converterName).newInstance();
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
