/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.mapper;

import org.openmrs.BaseOpenmrsData;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMapper<T, R extends BaseOpenmrsData> implements Mapper<T, R> {

    public List<T> toDtos(List<R> daos) {
        List<T> dtos = new ArrayList<T>();
        for (R dao : daos) {
            dtos.add(toDto(dao));
        }
        return dtos;
    }

    public List<R> fromDtos(List<T> dtos) {
        List<R> daos = new ArrayList<R>();
        for (T dto : dtos) {
            daos.add(fromDto(dto));
        }
        return daos;
    }
}
