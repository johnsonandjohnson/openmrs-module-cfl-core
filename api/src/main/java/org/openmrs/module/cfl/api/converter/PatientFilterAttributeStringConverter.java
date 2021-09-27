package org.openmrs.module.cfl.api.converter;

import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.dto.AdHocMessagePatientFilterDTO;
import org.openmrs.module.messages.domain.criteria.Condition;
import org.openmrs.module.messages.domain.criteria.PersonAttributeCondition;

import java.util.Collections;
import java.util.List;

public class PatientFilterAttributeStringConverter extends AbstractFieldValueStaticOperatorConverter {

    @Override
    protected List<Condition<?, ?>> createConditions(AdHocMessagePatientFilterDTO dto) {
        final PersonAttributeType personAttributeType =
                Context.getPersonService().getPersonAttributeTypeByName(getFieldPath());

        if (personAttributeType == null) {
            throw new IllegalStateException("Missing Person Attribute Type for name: " + getFieldPath());
        }

        return Collections.<Condition<?, ?>>singletonList(
                new PersonAttributeCondition(personAttributeType, getOperator(), dto.getValue()));
    }
}
