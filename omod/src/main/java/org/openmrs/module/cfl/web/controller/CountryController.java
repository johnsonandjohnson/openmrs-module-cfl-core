package org.openmrs.module.cfl.web.controller;

import liquibase.util.file.FilenameUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.io.UnicodeInputStream;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.cfl.api.service.CFLConceptService;
import org.openmrs.module.cfl.api.service.CountryService;
import org.openmrs.module.cfl.web.model.CountryControllerModel;
import org.openmrs.web.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The CountryController class
 */
@Controller("cfl.countryController")
@RequestMapping(value = "module/cfl")
public class CountryController {

    private static final String COUNTRIES_LIST_REDIRECT_VIEW = "/openmrs/module/cfl/countryList.form";

    private static final String COUNTRY_FORM_REDIRECT_VIEW = "/openmrs/module/cfl/countryForm.form";

    private static final String COUNTRY_IMPORT_FORM_REDIRECT_VIEW = "/openmrs/module/cfl/countryImport.form";

    private static final String COUNTRY_LIST_VIEW = "/module/cfl/countryList";

    private static final String ADD_COUNTRY_VIEW = "/module/cfl/countryForm";

    private static final String MODEL = "model";

    private static final String COUNTRY_CONCEPT_NAME = "Country";

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);

    @Autowired
    private ConceptService conceptService;

    @Autowired
    @Qualifier("cfl.conceptService")
    private CFLConceptService cflConceptService;

    @Autowired
    @Qualifier("cfl.countryService")
    private CountryService cflCountryService;

    @RequestMapping(value = "/countryList.form", method = RequestMethod.GET)
    public ModelAndView getCountryList() {
        Concept countryConcept = conceptService.getConceptByName(COUNTRY_CONCEPT_NAME);
        List<Concept> countryConcepts = cflConceptService.getConceptMembersByConcept(countryConcept);
        Map<Integer, String> conceptMap = new HashMap<>();
        countryConcepts.forEach(concept -> conceptMap.put(concept.getConceptId(),
                concept.getFullySpecifiedName(Locale.ENGLISH).getName()));

        return new ModelAndView(COUNTRY_LIST_VIEW, MODEL,
                new CountryControllerModel(sortCountriesAlphabetically(conceptMap)));
    }

    @RequestMapping(value = "/countryForm.form", method = RequestMethod.GET)
    public ModelAndView getCountryForm() {
        return new ModelAndView(ADD_COUNTRY_VIEW, MODEL, new CountryControllerModel());
    }

    @RequestMapping(value = "/countryForm.form", method = RequestMethod.POST)
    public ModelAndView addCountry(HttpServletRequest httpServletRequest, CountryControllerModel model) {
        Concept countryConcept = conceptService.getConceptByName(COUNTRY_CONCEPT_NAME);
        return createOrUpdateCountryConcept(httpServletRequest, countryConcept, model.getName());
    }

    @RequestMapping(value = "/countryImport.form", method = RequestMethod.GET)
    public ModelAndView getCountryImportForm() {
        return new ModelAndView();
    }

    @RequestMapping(value = "/countryImport.form", method = RequestMethod.POST)
    public ModelAndView importCountriesFromFile(HttpServletRequest httpServletRequest,
                                                @RequestParam(value = "file") MultipartFile file) {
        String returnViewName;
        List<String> duplicatedCountriesNames;
        try {
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (StringUtils.equals(fileExtension, "txt") || StringUtils.equals(fileExtension, "csv")) {
                List<String> countriesList = getCountriesListFromFile(file);
                duplicatedCountriesNames = cflCountryService.processCountriesAndReturnDuplicates(countriesList);
                checkDuplicatedCountriesAndSetProperInfoMsg(httpServletRequest, duplicatedCountriesNames);
                returnViewName = COUNTRIES_LIST_REDIRECT_VIEW;
            } else {
                setInfoMessage(httpServletRequest, "cfl.importCountries.wrongFormatFile.info");
                returnViewName = COUNTRY_IMPORT_FORM_REDIRECT_VIEW;
            }
        } catch (Exception ex) {
            setInfoMessage(httpServletRequest, "cfl.importCountries.failure.info");
            returnViewName = COUNTRY_IMPORT_FORM_REDIRECT_VIEW;
        }
        return new ModelAndView(new RedirectView(returnViewName));
    }

    private ModelAndView createOrUpdateCountryConcept(HttpServletRequest httpServletRequest,
                                                      Concept existingCountryConcept, String enteredCountryName) {
        String returnViewName;
        Concept enteredCountryConcept = conceptService.getConceptByName(enteredCountryName);
        List<Concept> countryConcepts = cflConceptService.getConceptMembersByConcept(existingCountryConcept);
        try {
            if (enteredCountryConcept == null) {
                Concept newCountryConcept = cflCountryService.buildAndSaveCountryResources(enteredCountryName);
                addCountryConceptToExistingCountriesList(existingCountryConcept, newCountryConcept);
                setInfoMessage(httpServletRequest, "cfl.addNewCountry.success.info");
                returnViewName = COUNTRIES_LIST_REDIRECT_VIEW;
            } else if (countryConcepts.contains(enteredCountryConcept)) {
                setInfoMessage(httpServletRequest, "cfl.addNewCountry.alreadyExists");
                returnViewName = COUNTRY_FORM_REDIRECT_VIEW;
            } else {
                addCountryConceptToExistingCountriesList(existingCountryConcept, enteredCountryConcept);
                setInfoMessage(httpServletRequest, "cfl.addNewCountry.success.info");
                returnViewName = COUNTRIES_LIST_REDIRECT_VIEW;
            }
        } catch (Exception ex) {
            setInfoMessage(httpServletRequest, "cfl.addNewCountry.failure.info");
            returnViewName = COUNTRY_FORM_REDIRECT_VIEW;
        }
        return new ModelAndView(new RedirectView(returnViewName));
    }

    private List<String> getCountriesListFromFile(MultipartFile file) {
        List<String> countryList = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new UnicodeInputStream(inputStream), StandardCharsets.UTF_8));
            reader.lines().filter(StringUtils::isNotBlank).forEach(countryList::add);
        } catch (IOException ex) {
            LOGGER.error("Unable to import file. " + ex.getMessage());
        }
        return countryList;
    }

    private Map<Integer, String> sortCountriesAlphabetically(Map<Integer, String> conceptMap) {
        return conceptMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private void checkDuplicatedCountriesAndSetProperInfoMsg(HttpServletRequest httpServletRequest,
                                                             List<String> duplicatedCountries) {
        if (CollectionUtils.isEmpty(duplicatedCountries)) {
            setInfoMessage(httpServletRequest, "cfl.importCountries.success.info");
        } else {
            setInfoMessage(httpServletRequest, String.format("The following duplicates were imported only once: %s",
                    String.join(", ", duplicatedCountries)));
        }
    }

    private void addCountryConceptToExistingCountriesList(Concept sourceConcept, Concept conceptToAdd) {
        sourceConcept.addSetMember(conceptToAdd);
        conceptService.saveConcept(sourceConcept);
    }

    private void setInfoMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, message);
    }
}
