package org.openmrs.module.cfl.api.converter;

import org.openmrs.module.cfl.api.dto.AdHocMessagePatientFilterDTO;
import org.openmrs.module.messages.domain.criteria.Condition;
import org.openmrs.module.messages.domain.criteria.PersonAddressCondition;

import java.util.Collections;
import java.util.List;

public class PatientFilterAddressFieldStringConverter extends AbstractFieldValueStaticOperatorConverter {

    @Override
    protected List<Condition<?, ?>> createConditions(AdHocMessagePatientFilterDTO dto) {
        return Collections.<Condition<?, ?>>singletonList(
                new PersonAddressCondition(getFieldPath(), getOperator(), dto.getValue()));
    }
}
