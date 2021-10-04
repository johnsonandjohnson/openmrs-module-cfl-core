package org.openmrs.module.cfl.api.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;

/**
 * The Address Hierarchy service with CFL-Specific functions.
 */
public interface CFLAddressHierarchyService {
    /**
     * Delete all Address Hierarchy Entries in a safe manner.
     * <p>
     * The method explicitly deletes all entries to make sure the MySQL limitations related to cascading are not reached.
     * </p>
     *
     * @see AddressHierarchyService#deleteAllAddressHierarchyEntries()
     */
    @Authorized({"Manage Address Hierarchy"})
    void safeDeleteAllAddressHierarchyEntries();
}
