/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.advice;

import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.service.CFLAddressHierarchyService;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class DeleteAllAddressHierarchyEntriesAdvice implements MethodBeforeAdvice {
    private static final String DELETE_ALL_ADDRESS_HIERARCHY_ENTRIES_METHOD_NAME = "deleteAllAddressHierarchyEntries";

    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        if (method.getName().equals(DELETE_ALL_ADDRESS_HIERARCHY_ENTRIES_METHOD_NAME)) {
            Context.getService(CFLAddressHierarchyService.class).safeDeleteAllAddressHierarchyEntries();
        }
    }
}
