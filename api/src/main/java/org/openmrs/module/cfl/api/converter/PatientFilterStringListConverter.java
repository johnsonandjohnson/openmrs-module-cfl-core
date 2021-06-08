package org.openmrs.module.cfl.api.converter;

import org.openmrs.module.cfl.api.dto.AdHocMessagePatientFilterDTO;
import org.openmrs.module.messages.domain.criteria.Condition;
import org.openmrs.module.messages.domain.criteria.EntityFieldCondition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PatientFilterStringListConverter extends AbstractFieldValueConverter
        implements PatientFilterConverterWithOptions {
    private List<String> options;

    @Override
    public void initFilter(Map<String, Object> properties) {
        super.initFilter(properties);
        options = (List<String>) properties.get(OPTIONS_PROP);

        if (options == null) {
            throw new IllegalArgumentException("The 'options' property must not be empty!");
        }
    }

    @Override
    protected List<Condition<?, ?>> createConditions(AdHocMessagePatientFilterDTO dto) {
        return Collections.<Condition<?, ?>>singletonList(
                new EntityFieldCondition(getFieldPath(), "IN", dto.getManyValues()));
    }

    @Override
    protected boolean isConfigurationInvalid() {
        return options == null;
    }

    @Override
    public List<String> getOptions() {
        return options;
    }
}
