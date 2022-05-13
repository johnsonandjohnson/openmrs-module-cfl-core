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

import org.apache.commons.lang.ObjectUtils;
import org.openmrs.module.cfl.api.dto.AdHocMessagePatientFilterDTO;
import org.openmrs.module.cfl.api.util.ConversionUtils;
import org.openmrs.module.cfl.domain.criteria.messages.ReceivedDosagesCondition;
import org.openmrs.module.messages.domain.criteria.Condition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PatientFilterReceivedDosagesConverter implements PatientFilterConverter {
    @Override
    public void initFilter(Map<String, Object> properties) {
        // nothing to do
    }

    @Override
    public List<Condition<?, ?>> convert(AdHocMessagePatientFilterDTO dto) {
        if (dto.getValue() == null || dto.getSecondValue() == null) {
            return Collections.emptyList();
        }

        return Collections.<Condition<?, ?>>singletonList(
                new ReceivedDosagesCondition(ObjectUtils.toString(dto.getSecondValue()),
                        ConversionUtils.toNumber(dto.getValue()).intValue()));
    }
}
