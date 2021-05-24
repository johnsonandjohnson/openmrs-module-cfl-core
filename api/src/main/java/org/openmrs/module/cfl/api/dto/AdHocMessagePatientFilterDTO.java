package org.openmrs.module.cfl.api.dto;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.cfl.api.contract.PatientFilterConfiguration;
import org.openmrs.module.cfl.api.converter.PatientFilterConverterWithOptions;
import org.springframework.util.AutoPopulatingList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The AdHocMessagePatientFilterDTO Class.
 * <p>
 * The single DTO class for all filters supported by teh Ad hoc message page.
 * </p>
 */
public class AdHocMessagePatientFilterDTO {
    private String label;
    private String filterTypeName;

    private List<Option> options;

    private Object value;
    private Object secondValue;
    private List<String> manyValues;

    public AdHocMessagePatientFilterDTO() {
        this.manyValues = new AutoPopulatingList<String>(String.class);
    }

    public AdHocMessagePatientFilterDTO(final PatientFilterConfiguration configuration) {
        this();
        this.label = configuration.getLabel();
        this.filterTypeName = configuration.getInputType().toString();

        this.options = readOptions(configuration.getConfig());
    }

    private List<Option> readOptions(final Map<String, Object> configurationProperties) {
        final Object optionsRawValue = configurationProperties.get(PatientFilterConverterWithOptions.OPTIONS_PROP);

        final List<Option> result = new ArrayList<Option>();

        if (optionsRawValue instanceof Iterable) {
            for (Object option : (Iterable) optionsRawValue) {
                final String optionLabel;
                final Object optionValue;

                if (option instanceof Map) {
                    final Map<String, Object> optionMap = (Map<String, Object>) option;
                    optionLabel = (String) optionMap.get(Option.LABEL);
                    optionValue = optionMap.get(Option.VALUE);
                } else {
                    optionLabel = option.toString();
                    optionValue = option;
                }

                result.add(new Option(optionLabel, optionValue));
            }
        }

        return result;
    }

    public void copyValue(AdHocMessagePatientFilterDTO otherDto) {
        this.value = otherDto.getValue();
        this.secondValue = otherDto.getSecondValue();
        this.manyValues = new AutoPopulatingList<String>(String.class);

        if (otherDto.getManyValues() != null) {
            this.manyValues.addAll(otherDto.getManyValues());
        }
    }

    public boolean hasValue() {
        return value != null || secondValue != null || CollectionUtils.isNotEmpty(manyValues);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFilterTypeName() {
        return filterTypeName;
    }

    public void setFilterTypeName(String filterTypeName) {
        this.filterTypeName = filterTypeName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (value instanceof String && StringUtils.isBlank((String) value)) {
            // Blank value means no value
            return;
        }

        this.value = value;
    }

    public Object getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(Object secondValue) {
        if (secondValue instanceof String && StringUtils.isBlank((String) secondValue)) {
            // Blank value means no value
            return;
        }

        this.secondValue = secondValue;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public List<String> getManyValues() {
        return manyValues;
    }

    public void setManyValues(List<String> manyValues) {
        this.manyValues = manyValues;
    }

    public static class Option {
        static final String LABEL = "label";
        static final String VALUE = "value";

        private final String label;
        private final Object value;

        Option(String label, Object value) {
            this.label = label;
            this.value = value == null ? label : value;
        }

        public String getLabel() {
            return label;
        }

        public Object getValue() {
            return value;
        }
    }
}
