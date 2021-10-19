package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.cfl.api.builder.ConceptBuilder;
import org.openmrs.module.cfl.api.builder.ConceptNameBuilder;
import org.openmrs.module.cfl.api.service.CFLConceptService;
import org.openmrs.module.cfl.api.service.CountryService;
import org.springframework.transaction.annotation.Transactional;

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

    private CFLConceptService cflConceptService;

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
                .withConceptName(name)
                .build();

        return conceptService.saveConcept(newCountryConcept);
    }

    @Override
    @Transactional
    public List<String> processCountriesAndReturnDuplicates(List<String> countriesList) {
        List<String> duplicatedCountriesNames = new ArrayList<>();
        Concept countryConcept = conceptService.getConceptByName(COUNTRY_CONCEPT_NAME);
        List<Concept> countryConcepts = cflConceptService.getConceptMembersByConcept(countryConcept);

        countriesList.forEach(country -> {
            Concept enteredCountryConcept = conceptService.getConceptByName(country);
            if (enteredCountryConcept == null) {
                Concept newCountryConcept = buildAndSaveCountryResources(country);
                countryConcept.addSetMember(newCountryConcept);
            } else if (countryConcepts.contains(enteredCountryConcept)) {
                duplicatedCountriesNames.add(country);
            } else {
                countryConcept.addSetMember(enteredCountryConcept);
            }
        });
        return duplicatedCountriesNames;
    }

    public void setConceptService(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public void setCflConceptService(CFLConceptService cflConceptService) {
        this.cflConceptService = cflConceptService;
    }
}
