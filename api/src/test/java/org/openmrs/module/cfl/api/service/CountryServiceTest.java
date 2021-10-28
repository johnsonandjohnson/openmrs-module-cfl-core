package org.openmrs.module.cfl.api.service;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CountryServiceTest extends BaseModuleContextSensitiveTest {

  private static final String COUNTRIES_LIST = "CountriesList.txt";

  @Autowired
  @Qualifier("cfl.countryService")
  private CountryService countryService;

  @Before
  public void setUp() throws Exception {
    executeDataSet("datasets/CountryServiceTest.xml");
  }

  @Test
  public void shouldSuccessfullyBuildAndSaveCountry() {
    Concept result = countryService.buildAndSaveCountryResources("Test Country Name");

    assertNotNull(result);
    assertEquals("Test Country Name", result.getFullySpecifiedName(Locale.ENGLISH).getName());
    assertEquals("N/A", result.getDatatype().getName());
    assertEquals("ConvSet", result.getConceptClass().getName());
    assertTrue(result.getSet());
  }

  @Test
  public void shouldSuccessfullyProcessCountriesAndReturnDuplicates() {
    Map<String, String> countriesMap = new HashMap<>();
    countriesMap.put("Poland", "member1");
    countriesMap.put("China", "member1");
    countriesMap.put("Belgium", "member1");

    List<String> result = countryService.processAndReturnAlreadyExistingCountries(countriesMap);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains("Poland"));
    assertTrue(result.contains("Belgium"));
    assertFalse(result.contains("China"));
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
