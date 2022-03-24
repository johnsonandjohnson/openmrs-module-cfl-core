package org.openmrs.module.cfl.db;

import org.openmrs.module.addresshierarchy.db.AddressHierarchyDAO;
import org.openmrs.module.cfl.api.dto.AddressDataDTO;

/**
 * The ExtendedAddressHierarchyDAO Class.
 *
 * <p>This DAO contains additional, CfL-special data access functions for Address Hierarchy
 * entities.
 *
 * <p>The caller of ant method of this class has to take care of running an active transaction.
 *
 * @see org.openmrs.module.addresshierarchy.db.AddressHierarchyDAO
 */
public interface ExtendedAddressHierarchyDAO {
  /**
   * Deletes all Address Hierarchy Entries safely.
   *
   * <p>Compared to {@link AddressHierarchyDAO#deleteAllAddressHierarchyEntries()}, this method
   * deletes all entries explicitly and uses SQL queries to prevent hitting any limitations for DB
   * cascade operations.
   *
   * @see AddressHierarchyDAO#deleteAllAddressHierarchyEntries()
   */
  void deleteAllAddressHierarchyEntriesSafely();

  /** Deletes all address hierarchy levels */
  void deleteAllAddressHierarchyLevels();

  /**
   * Gets {@link AddressDataDTO} object with paginated address data needed to be shown on UI
   *
   * @param pageNumber page number
   * @param pageSize page size
   * @return AddressDataDTO object
   */
  AddressDataDTO getAddressData(Integer pageNumber, Integer pageSize);

  /**
   * Gets total count of all address data results
   *
   * @return number of results
   */
  int getCountAllAddressData();
}
