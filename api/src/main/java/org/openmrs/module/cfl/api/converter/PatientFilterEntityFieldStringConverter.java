package org.openmrs.module.cfl.api.converter;

import org.openmrs.module.cfl.api.dto.AdHocMessagePatientFilterDTO;
import org.openmrs.module.messages.domain.criteria.Condition;
import org.openmrs.module.messages.domain.criteria.EntityFieldCondition;

import java.util.Collections;
import java.util.List;

public class PatientFilterEntityFieldStringConverter extends AbstractFieldValueStaticOperatorConverter {

    @Override
    protected List<Condition<?, ?>> createConditions(AdHocMessagePatientFilterDTO dto) {
        return Collections.<Condition<?, ?>>singletonList(
                new EntityFieldCondition(getFieldPath(), getOperator(), dto.getValue()));
    }
}
