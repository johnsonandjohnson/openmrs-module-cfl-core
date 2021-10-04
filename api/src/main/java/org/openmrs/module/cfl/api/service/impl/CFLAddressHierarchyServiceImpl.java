package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.cfl.api.service.CFLAddressHierarchyService;
import org.openmrs.module.cfl.db.ExtendedAddressHierarchyDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of CFLAddressHierarchyService.
 */
public class CFLAddressHierarchyServiceImpl extends BaseOpenmrsService implements CFLAddressHierarchyService {
    private ExtendedAddressHierarchyDAO extendedAddressHierarchyDAO;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void safeDeleteAllAddressHierarchyEntries() {
        extendedAddressHierarchyDAO.deleteAllAddressHierarchyEntriesSafely();
        // Extract from context, because bean in not named in address hierarchy
        Context.getService(AddressHierarchyService.class).resetFullAddressCache();
    }

    public void setExtendedAddressHierarchyDAO(ExtendedAddressHierarchyDAO extendedAddressHierarchyDAO) {
        this.extendedAddressHierarchyDAO = extendedAddressHierarchyDAO;
    }
}
