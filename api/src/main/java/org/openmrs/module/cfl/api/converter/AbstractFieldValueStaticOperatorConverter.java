package org.openmrs.module.cfl.api.converter;

import org.openmrs.module.cfl.api.dto.AdHocMessagePatientFilterDTO;
import org.openmrs.module.messages.domain.criteria.Condition;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

public abstract class AbstractFieldValueStaticOperatorConverter extends AbstractFieldValueConverter
        implements PatientFilterConverterWithOperator {

    private String operator;

    @Override
    public void initFilter(Map<String, Object> properties) {
        super.initFilter(properties);
        operator = (String) properties.get(OPERATOR_PROP);

        if (operator == null) {
            throw new IllegalArgumentException(
                    "The following properties must not be empty: " + Arrays.asList(FIELD_PATH_PROP, OPERATOR_PROP));
        }
    }

    @Override
    public String getOperator() {
        return operator;
    }

    protected abstract List<Condition<?, ?>> createConditions(AdHocMessagePatientFilterDTO dto);

    @Override
    protected boolean isConfigurationInvalid() {
        return super.isConfigurationInvalid() || isBlank(operator);
    }
}
