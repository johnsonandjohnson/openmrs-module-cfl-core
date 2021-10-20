package org.openmrs.module.cfl.api.service;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
        assertEquals("Misc", result.getConceptClass().getName());
        assertFalse(result.getSet());
    }

    @Test
    public void shouldSuccessfullyProcessCountriesAndReturnDuplicates() {
        List<String> countries = Arrays.asList("Poland", "China", "Belgium");

        List<String> result = countryService.processAndReturnAlreadyExistingCountries(countries);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Poland"));
        assertTrue(result.contains("Belgium"));
        assertFalse(result.contains("China"));
    }

    @Test
    public void shouldGetCountriesFromFile() throws IOException {
        InputStream inputStream = getInputStreamFromFile(COUNTRIES_LIST);

        List<String> result = countryService.getCountriesListFromFile(inputStream);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.contains("Poland"));
        assertTrue(result.contains("Belgium"));
        assertTrue(result.contains("India"));
        assertTrue(result.contains("China"));
        assertTrue(result.contains("Argentina"));
    }

    private InputStream getInputStreamFromFile(String fileName) {
        return this.getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
