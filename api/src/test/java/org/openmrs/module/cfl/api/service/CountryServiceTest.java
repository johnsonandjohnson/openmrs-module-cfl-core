/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.service;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CountryServiceTest extends BaseModuleContextSensitiveTest {

  private static final String COUNTRIES_LIST = "CountriesList.txt";

  @Autowired
  @Qualifier("cfl.countryService")
  private CountryService countryService;

  @Autowired
  private ConceptService conceptService;

  @Before
  public void setUp() throws Exception {
    executeDataSet("datasets/CountryServiceTest.xml");
  }

  @Test
  public void shouldSuccessfullyBuildAndSaveCountryWithoutCountryCode() {
    Concept result = countryService.buildAndSaveCountryResources("Test Country Name", null);

    assertNotNull(result);
    assertEquals("Test Country Name", result.getName().getName());
    assertEquals("N/A", result.getDatatype().getName());
    assertEquals("Misc", result.getConceptClass().getName());
    assertNull(result.getShortNameInLocale(Locale.ENGLISH));
    assertTrue(result.getSet());
  }

  @Test
  public void shouldSuccessfullyBuildAndSaveCountryWithCountryCode() {
    Concept result = countryService.buildAndSaveCountryResources("Test Country Name", "TCN");

    assertNotNull(result);
    assertEquals("Test Country Name", result.getName().getName());
    assertEquals("N/A", result.getDatatype().getName());
    assertEquals("Misc", result.getConceptClass().getName());
    assertEquals("TCN", result.getShortNameInLocale(Locale.ENGLISH).getName());
    assertTrue(result.getSet());
  }

  @Test
  public void shouldGetCountriesResourcesFromFile() throws IOException {
    InputStream inputStream = getInputStreamFromFile(COUNTRIES_LIST);

    Map<String, String> resultMap = countryService.getCountriesListFromFile(inputStream);
    List<String> countriesList = new ArrayList<>(resultMap.keySet());

    assertNotNull(resultMap);
    assertEquals(5, resultMap.size());
    assertEquals(5, countriesList.size());
    assertTrue(countriesList.contains("Poland"));
    assertTrue(countriesList.contains("Belgium"));
    assertTrue(countriesList.contains("India"));
    assertTrue(countriesList.contains("China"));
    assertTrue(countriesList.contains("Argentina"));

    String indiaMembers = resultMap.get("India");
    assertNotNull(indiaMembers);
    assertEquals("member1,member2,member3", indiaMembers);
  }

  private InputStream getInputStreamFromFile(String fileName) {
    return this.getClass().getClassLoader().getResourceAsStream(fileName);
  }
}
