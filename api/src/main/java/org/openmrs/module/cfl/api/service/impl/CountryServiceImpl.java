package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Default implementation of {@link CountryService} */
public class CountryServiceImpl implements CountryService {

  private static final String NOT_ASSOCIATED_DATATYPE_NAME = "N/A";

  private static final String MISC_CONCEPT_CLASS_NAME = "Misc";

  private static final String COUNTRY_CONCEPT_NAME = "Country";

  private static final Log LOGGER = LogFactory.getLog(CountryServiceImpl.class);

  private ConceptService conceptService;

  @Override
  @Transactional
  public Concept buildAndSaveCountryResources(String name, String shortName) {
    ConceptName fullyConceptName = new ConceptNameBuilder().withName(name).build();
    ConceptName shortConceptName =
        new ConceptNameBuilder()
            .withName(shortName)
            .withConceptNameType(ConceptNameType.SHORT)
            .build();

    Concept newCountryConcept =
        new ConceptBuilder()
            .withConceptDatatype(
                conceptService.getConceptDatatypeByName(NOT_ASSOCIATED_DATATYPE_NAME))
            .withConceptClass(conceptService.getConceptClassByName(MISC_CONCEPT_CLASS_NAME))
            .withIsSet(true)
            .withFullySpecifiedName(fullyConceptName)
            .withShortName(shortConceptName)
            .build();

    return conceptService.saveConcept(newCountryConcept);
  }

  @Override
  @Transactional
  public List<String> processAndReturnAlreadyExistingCountries(
      Map<String, String> countriesResourcesMap) {
    List<String> duplicatedCountriesNames = new ArrayList<>();
    Concept countryConcept = conceptService.getConceptByName(COUNTRY_CONCEPT_NAME);
    if (countryConcept != null) {
      duplicatedCountriesNames =
          processCountriesResourcesMap(countriesResourcesMap, countryConcept);
    } else {
      LOGGER.warn(String.format("Concept with name %s not found", COUNTRY_CONCEPT_NAME));
    }

    return duplicatedCountriesNames;
  }

  @Override
  @Transactional
  public Map<String, String> getCountriesListFromFile(InputStream inputStream) throws IOException {
    Map<String, String> countriesResourcesList = new HashMap<>();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      reader
          .lines()
          .filter(StringUtils::isNotBlank)
          .forEach(
              line -> {
                if (line.contains(":")) {
                  String[] splittedLine = line.split(":");
                  countriesResourcesList.put(splittedLine[0].trim(), splittedLine[1].trim());
                } else {
                  LOGGER.info(
                      String.format(
                          "This line will be skipped %s because does not contain appropriate delimiter",
                          line));
                }
              });
    } catch (Exception ex) {
      LOGGER.error("Unable to import file");
    }
    return countriesResourcesList;
  }

  @Override
  @Transactional
  public void createClusterMembersResources(String clusterMembersList, Concept countryConcept) {
    Arrays.stream(clusterMembersList.split(","))
        .filter(StringUtils::isNotBlank)
        .map(name -> getOrCreateCountryConcept(name.trim()))
        .forEach(clusterMember -> addCountryClusterMember(countryConcept, clusterMember));
  }

  @Override
  @Transactional
  public Concept getOrCreateCountryConcept(String name) {
    Concept concept = conceptService.getConceptByName(name);
    if (concept == null) {
      concept = buildAndSaveCountryResources(name, null);
    } else if (BooleanUtils.isFalse(concept.getSet())) {
      concept.setSet(true);
      conceptService.saveConcept(concept);
    }

    return concept;
  }

  private List<String> processCountriesResourcesMap(
      Map<String, String> countriesResourcesMap, Concept countryConcept) {
    List<String> duplicatedCountriesNames = new ArrayList<>();
    countriesResourcesMap.forEach(
        (key, value) -> {
          Concept concept = getOrCreateCountryConcept(key);
          createClusterMembersResources(value, concept);
          if (countryConcept.getSetMembers().contains(concept)) {
            duplicatedCountriesNames.add(key);
          } else {
            countryConcept.addSetMember(concept);
          }
        });
    return duplicatedCountriesNames;
  }

  private void addCountryClusterMember(Concept countryConcept, Concept clusterMember) {
    if (!countryConcept.getSetMembers().contains(clusterMember)) {
      countryConcept.addSetMember(clusterMember);
    }
  }

  public void setConceptService(ConceptService conceptService) {
    this.conceptService = conceptService;
  }
}
