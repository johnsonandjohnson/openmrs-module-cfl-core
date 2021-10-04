package org.openmrs.module.cfl.advice;

import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.service.CFLAddressHierarchyService;
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
