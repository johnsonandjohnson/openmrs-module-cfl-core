package org.openmrs.module.cfl.db;

import org.openmrs.module.addresshierarchy.db.AddressHierarchyDAO;

/**
 * The ExtendedAddressHierarchyDAO Class.
 * <p>
 * This DAO contains additional, CfL-special data access functions for Address Hierarchy entities.
 * </p>
 * <p>
 * The caller of ant method of this class has to take care of running an active transaction.
 * </p>
 *
 * @see org.openmrs.module.addresshierarchy.db.AddressHierarchyDAO
 */
public interface ExtendedAddressHierarchyDAO {
    /**
     * Delete all Address Hierarchy Entries safely.
     * <p>
     * Compared to {@link AddressHierarchyDAO#deleteAllAddressHierarchyEntries()}, this method deletes all entries
     * explicitly and uses SQL queries to prevent hitting any limitations for DB cascade operations.
     * </p>
     *
     * @see AddressHierarchyDAO#deleteAllAddressHierarchyEntries()
     */
    void deleteAllAddressHierarchyEntriesSafely();
}
