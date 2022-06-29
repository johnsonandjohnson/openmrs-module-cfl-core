/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.db.AddressHierarchyDAO;
import org.openmrs.module.cflcore.api.dto.AddressDataDTO;
import org.openmrs.module.cflcore.api.dto.ImportDataResultDTO;
import org.openmrs.module.cflcore.api.model.AddressDataContent;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CFLAddressHierarchyServiceITTest extends BaseModuleContextSensitiveTest {

  @Autowired
  @Qualifier("cfl.CFLAddressHierarchyService")
  private CFLAddressHierarchyService cflAddressHierarchyService;

  private static final String MAIN_ADDRESS_DATA = "MainAddressData.txt";

  private static final String ADDITIONAL_ADDRESS_DATA = "AdditionalAddressData.txt";

  private static final String DEFAULT_DELIMITER = ",";

  private static final Integer DEFAULT_START_PAGE_NUMBER = 1;

  private static final Integer DEFAULT_PAGE_SIZE = 50;

  @Autowired private AddressHierarchyDAO addressHierarchyDAO;

  @Before
  public void setUp() throws Exception {
    executeDataSet("datasets/AddressHierarchyLevels.xml");
  }

  @Test
  public void shouldCreateAddressEntriesAndReturnInvalidRows() throws IOException {
    InputStream stream = getInputStreamFromFile(MAIN_ADDRESS_DATA);

    final ImportDataResultDTO result =
        cflAddressHierarchyService.importAddressHierarchyEntriesAndReturnInvalidRows(
            stream, DEFAULT_DELIMITER, true);

    assertNotNull(result);
    assertEquals(3, result.getNumberOfInvalidRecords());
    assertEquals(
        "First,,Invalid_Row,-> Reason: The line has empty cells.",
        result.getInvalidRecords().get(0));
    assertEquals(
        ",,Second,Invalid,,Row,-> Reason: Too much fields. The allowed number of fields is 5. The line has empty cells.",
        result.getInvalidRecords().get(1));
    assertEquals(
        "Third,Invalid,Row,Too,Many,Fields,In,One,Row,-> Reason: Too much fields. The allowed number of fields is 5.",
        result.getInvalidRecords().get(2));
  }

  @Test
  public void shouldReturnTwoCitiesWithEqualNameButDifferentState() throws IOException {
    cflAddressHierarchyService.importAddressHierarchyEntriesAndReturnInvalidRows(
            getInputStreamFromFile(MAIN_ADDRESS_DATA), DEFAULT_DELIMITER, true);

    final AddressDataDTO result =
            cflAddressHierarchyService.getAddressDataResults(
                    DEFAULT_START_PAGE_NUMBER, DEFAULT_PAGE_SIZE);

    assertNotNull(result);
    final List<AddressDataContent> resultContent = result.getContent();
    assertTrue(
            "Should contain data for line: KrajA,DystryktA,MiastoA",
            resultContent.contains(
                    new AddressDataContent(new String[]{"KrajA", "DystryktA", "MiastoA", null, null})));
    assertTrue(
            "Should contain data for line: KrajA,DystryktB,MiastoA",
            resultContent.contains(
                    new AddressDataContent(new String[]{"KrajA", "DystryktB", "MiastoA", null, null})));
  }

  @Test
  public void shouldReturnAddressDataWhenDataAreOverwritten() throws IOException {
    cflAddressHierarchyService.importAddressHierarchyEntriesAndReturnInvalidRows(
        getInputStreamFromFile(MAIN_ADDRESS_DATA), DEFAULT_DELIMITER, true);

    final AddressDataDTO result =
        cflAddressHierarchyService.getAddressDataResults(
            DEFAULT_START_PAGE_NUMBER, DEFAULT_PAGE_SIZE);

    assertNotNull(result);
    assertEquals(1, result.getPageNumber());
    assertEquals(50, result.getPageSize());
    assertEquals(12, result.getTotalCount());
    assertFalse(result.isNextPage());
    assertEquals(12, result.getContent().size());
  }

  @Test
  public void shouldReturnAddressDataWhenDataAreAddedToExistingData() throws IOException {
    cflAddressHierarchyService.importAddressHierarchyEntriesAndReturnInvalidRows(
        getInputStreamFromFile(MAIN_ADDRESS_DATA), DEFAULT_DELIMITER, true);
    cflAddressHierarchyService.importAddressHierarchyEntriesAndReturnInvalidRows(
        getInputStreamFromFile(ADDITIONAL_ADDRESS_DATA), DEFAULT_DELIMITER, false);

    final AddressDataDTO result =
        cflAddressHierarchyService.getAddressDataResults(
            DEFAULT_START_PAGE_NUMBER, DEFAULT_PAGE_SIZE);

    assertNotNull(result);
    assertEquals(1, result.getPageNumber());
    assertEquals(50, result.getPageSize());
    assertEquals(15, result.getTotalCount());
    assertFalse(result.isNextPage());
    assertEquals(15, result.getContent().size());
  }

  @Test
  public void shouldReturnPaginatedAddressData() throws IOException {
    cflAddressHierarchyService.importAddressHierarchyEntriesAndReturnInvalidRows(
        getInputStreamFromFile(MAIN_ADDRESS_DATA), DEFAULT_DELIMITER, true);

    final AddressDataDTO result = cflAddressHierarchyService.getAddressDataResults(1, 5);

    assertNotNull(result);
    assertEquals(1, result.getPageNumber());
    assertEquals(5, result.getPageSize());
    assertEquals(12, result.getTotalCount());
    assertTrue(result.isNextPage());
    assertEquals(5, result.getContent().size());
  }

  @Test
  public void shouldNotCreateNewEntryIfAlreadyExist() throws IOException {
    final InputStream stream = new ByteArrayInputStream("Poland".getBytes(StandardCharsets.UTF_8));

    cflAddressHierarchyService.importAddressHierarchyEntriesAndReturnInvalidRows(
        stream, DEFAULT_DELIMITER, false);
    final AddressHierarchyLevel level = addressHierarchyDAO.getAddressHierarchyLevel(1);
    final List<AddressHierarchyEntry> countryEntries =
        addressHierarchyDAO.getAddressHierarchyEntriesByLevel(level);

    assertEquals(1, countryEntries.size());
  }

  private InputStream getInputStreamFromFile(String fileName) {
    return this.getClass().getClassLoader().getResourceAsStream(fileName);
  }
}
