/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.converter;

import org.openmrs.module.cflcore.api.dto.AdHocMessagePatientFilterDTO;
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
