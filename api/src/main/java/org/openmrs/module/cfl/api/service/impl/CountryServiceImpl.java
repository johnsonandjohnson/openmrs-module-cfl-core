package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.cfl.api.builder.ConceptBuilder;
import org.openmrs.module.cfl.api.builder.ConceptNameBuilder;
import org.openmrs.module.cfl.api.service.CountryService;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Default implementation of {@link CountryService}
 */
public class CountryServiceImpl implements CountryService {

    private static final String NOT_ASSOCIATED_DATATYPE_NAME = "N/A";

    private static final String MISC_CLASS_NAME = "Misc";

    private static final String COUNTRY_CONCEPT_NAME = "Country";

    private ConceptService conceptService;

    @Override
    @Transactional
    public Concept buildAndSaveCountryResources(String countryName) {
        ConceptName name = new ConceptNameBuilder()
                .withName(countryName)
                .withConceptNameType(ConceptNameType.FULLY_SPECIFIED)
                .withLocale(Locale.ENGLISH)
                .build();

        Concept newCountryConcept = new ConceptBuilder()
                .withConceptDatatype(conceptService.getConceptDatatypeByName(NOT_ASSOCIATED_DATATYPE_NAME))
                .withConceptClass(conceptService.getConceptClassByName(MISC_CLASS_NAME))
                .withIsSet(false)
                .withFullySpecifiedName(name)
                .build();

        return conceptService.saveConcept(newCountryConcept);
    }

    @Override
    @Transactional
    public List<String> processAndReturnAlreadyExistingCountries(List<String> countriesList) {
        List<String> duplicatedCountriesNames = new ArrayList<>();
        Concept countryConcept = conceptService.getConceptByName(COUNTRY_CONCEPT_NAME);

        List<Concept> countryConcepts = conceptService.getConceptsByConceptSet(countryConcept);

        countriesList.forEach(countryName -> {
            Concept concept = getOrCreateCountryConcept(countryName);
            if (countryConcepts.contains(concept)) {
                duplicatedCountriesNames.add(countryName);
            } else {
                countryConcept.addSetMember(concept);
            }
        });

        return duplicatedCountriesNames;
    }

    @Override
    @Transactional
    public List<String> getCountriesListFromFile(InputStream inputStream) throws IOException {
        List<String> countryList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            reader.lines().filter(StringUtils::isNotBlank).forEach(countryList::add);
        }
        return countryList;
    }

    private Concept getOrCreateCountryConcept(String countryName) {
        Concept concept = conceptService.getConceptByName(countryName);
        if (concept == null) {
            concept = buildAndSaveCountryResources(countryName);
        }
        return concept;
    }

    public void setConceptService(ConceptService conceptService) {
        this.conceptService = conceptService;
    }
}
