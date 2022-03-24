package org.openmrs.module.cfl.api.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.cfl.api.dto.AddressDataDTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** The Address Hierarchy service with CFL-Specific functions. */
public interface CFLAddressHierarchyService {
  /**
   * Deletes all Address Hierarchy Entries in a safe manner.
   *
   * <p>The method explicitly deletes all entries to make sure the MySQL limitations related to
   * cascading are not reached.
   *
   * @see AddressHierarchyService#deleteAllAddressHierarchyEntries()
   */
  @Authorized({"Manage Address Hierarchy"})
  void safeDeleteAllAddressHierarchyEntries();

  /**
   * Imports all Address Hierarchy Entries from file and returns invalid lines
   *
   * @param inputStream stream of input resource
   * @param delimiter delimiter character that separates the fields in each line of file
   * @param overwriteData determines if all existing data will be overwritten
   * @return list of invalid lines
   * @throws IOException thrown when an error occurred while reading the file
   */
  List<String> importAddressHierarchyEntriesAndReturnInvalidRows(
      InputStream inputStream, String delimiter, boolean overwriteData) throws IOException;

  /**
   * Gets {@link AddressDataDTO} object with paginated address data needed to be shown on UI
   *
   * @param pageNumber page number
   * @param pageSize page size
   * @return AddressDataDTO object
   */
  AddressDataDTO getAddressDataResults(Integer pageNumber, Integer pageSize);

  /**
   * Gets total count of all address data results
   *
   * @return number of results
   */
  int getCountAllAddressData();

  /** Creates all address hierarchy levels */
  void createAddressHierarchyLevels();

  /** Deletes all address hierarchy levels */
  void deleteAllAddressHierarchyLevels();

  /** Recreates all address hierarchy levels i.e. deletes and creates again */
  void recreateAddressHierarchyLevels();

  /**
   * Saves Address Hierarchy Entries in batches
   *
   * @param entries entries to save
   */
  void saveAddressHierarchyEntriesInBatches(List<AddressHierarchyEntry> entries);
}
