package org.openmrs.module.cfl.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import liquibase.util.file.FilenameUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.builder.ConceptNameBuilder;
import org.openmrs.module.cfl.api.service.CountryService;
import org.openmrs.module.cfl.web.model.CountryControllerModel;
import org.openmrs.web.WebConstants;
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
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** The CountryController class */
@Api(value = "Country", tags = {"REST API to handle Country data"})
@Controller("cfl.countryController")
@RequestMapping(value = "module/cfl")
public class CountryController {

  private static final String COUNTRIES_LIST_REDIRECT_VIEW = "/openmrs/module/cfl/countryList.form";

  private static final String COUNTRY_FORM_REDIRECT_VIEW = "/openmrs/module/cfl/countryForm.form";

  private static final String COUNTRY_IMPORT_FORM_REDIRECT_VIEW =
      "/openmrs/module/cfl/countryImport.form";

  private static final String COUNTRY_LIST_VIEW = "/module/cfl/countryList";

  private static final String ADD_COUNTRY_VIEW = "/module/cfl/countryForm";

  private static final String EDIT_COUNTRY_VIEW = "/module/cfl/editCountryForm";

  private static final String MODEL = "model";

  private static final String COUNTRY_CONCEPT_NAME = "Country";

  @Autowired private ConceptService conceptService;

  @Autowired
  @Qualifier("cfl.countryService")
  private CountryService countryService;

  @ApiOperation(value = "Get country list", notes = "Get country list")
  @ApiResponses(value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful getting country list"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to get country list"),
          @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in getting country list")})
  @RequestMapping(value = "/countryList.form", method = RequestMethod.GET)
  public ModelAndView getCountryList() {
    Concept countryConcept = conceptService.getConceptByName(COUNTRY_CONCEPT_NAME);
    Map<Integer, String> conceptMap = new HashMap<>();

    countryConcept
        .getSetMembers()
        .forEach(concept -> conceptMap.put(concept.getConceptId(), concept.getName().getName()));

    return new ModelAndView(
        COUNTRY_LIST_VIEW,
        MODEL,
        new CountryControllerModel(sortCountriesAlphabetically(conceptMap)));
  }

  @ApiOperation(value = "Get country form", notes = "Get country form")
  @ApiResponses(value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful getting country form"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to get country form"),
          @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in getting country form")})
  @RequestMapping(value = "/countryForm.form", method = RequestMethod.GET)
  public ModelAndView getCountryForm() {
    return new ModelAndView(ADD_COUNTRY_VIEW, MODEL, new CountryControllerModel());
  }

  @ApiOperation(value = "Add country", notes = "add country")
  @ApiResponses(value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful adding country"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to add country"),
          @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error adding country")})
  @RequestMapping(value = "/countryForm.form", method = RequestMethod.POST)
  public ModelAndView addCountry(
          @ApiParam(name = "httpServletRequest", value = "Servlet request") HttpServletRequest httpServletRequest,
          @ApiParam(name = "model", value = "Country model") CountryControllerModel model) {
    Concept countryConcept = conceptService.getConceptByName(COUNTRY_CONCEPT_NAME);
    return createOrUpdateCountryConcept(httpServletRequest, countryConcept, model);
  }

  @ApiOperation(value = "Get edit country page", notes = "Get edit country page")
  @ApiResponses(value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful getting edit country page"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to get edit country page"),
          @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in getting edit country page")})
  @RequestMapping(value = "/editCountryForm.form", method = RequestMethod.GET)
  public ModelAndView getEditCountryPage(
          @ApiParam(name = "conceptId", value = "Concept Id") @RequestParam(value = "conceptId") Integer conceptId) {
    Concept concept = conceptService.getConcept(conceptId);
    String clusterMembers = getClusterMembersByCountry(concept);

    return new ModelAndView(
        EDIT_COUNTRY_VIEW, MODEL, new CountryControllerModel(concept, clusterMembers));
  }

  @ApiOperation(value = "Edit country page", notes = "Edit country page")
  @ApiResponses(value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful editing country page"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to edit country page"),
          @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in editing country page")})
  @RequestMapping(value = "/editCountryForm.form", method = RequestMethod.POST)
  public ModelAndView editCountry(
          @ApiParam(name = "conceptId", value = "Concept Id") @RequestParam(value = "conceptId") Integer conceptId,
          @ApiParam(name = "httpServletRequest", value = "Servlet request") HttpServletRequest httpServletRequest,
          @ApiParam(name = "model", value = "Country model") CountryControllerModel model) {
    Concept countryConcept = conceptService.getConcept(conceptId);

    List<String> previousClusterMembersNames = getPreviousClusterMembersNames(countryConcept);
    List<String> actualClusterMembersNames =
        getActualClusterMembersNames(model.getClusterMembers());

    handleCountryNameChange(countryConcept, model.getName());
    handleCountryCodeChange(countryConcept, model.getCountryCode());
    handleClusterMembersChanges(
        countryConcept, previousClusterMembersNames, actualClusterMembersNames);

    setInfoMessage(httpServletRequest, "cfl.addNewCountry.success.info");
    return new ModelAndView(new RedirectView(COUNTRIES_LIST_REDIRECT_VIEW));
  }

  @ApiOperation(value = "Country import form", notes = "Country import form")
  @ApiResponses(value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful getting Country import form"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to get Country import form"),
          @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in getting Country import form")})
  @RequestMapping(value = "/countryImport.form", method = RequestMethod.GET)
  public ModelAndView getCountryImportForm() {
    return new ModelAndView();
  }

  @ApiOperation(value = "Import Country", notes = "Import Country")
  @ApiResponses(value = {
          @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful importing Country"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to import Country"),
          @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error in importing Country")})
  @RequestMapping(value = "/countryImport.form", method = RequestMethod.POST)
  public ModelAndView importCountriesFromFile(
          @ApiParam(name = "httpServletRequest", value = "Servlet request") HttpServletRequest httpServletRequest,
          @ApiParam(name = "file", value = "File containing country details")
          @RequestParam(value = "file") MultipartFile file) {
    String returnViewName;
    List<String> duplicatedCountriesNames;
    try {
      String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
      if (StringUtils.equals(fileExtension, "txt") || StringUtils.equals(fileExtension, "csv")) {
        Map<String, String> countriesMap =
            countryService.getCountriesListFromFile(file.getInputStream());
        duplicatedCountriesNames =
            countryService.processAndReturnAlreadyExistingCountries(countriesMap);
        handleCountriesFromFile(countriesMap);
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

  private void handleCountriesFromFile(Map<String, String> countriesMap) {
    countriesMap.forEach(
        (key, value) -> {
          Concept concept = conceptService.getConceptByName(key.trim());
          handleClusterMembersChanges(
              concept,
              getPreviousClusterMembersNames(concept),
              getActualClusterMembersNames(value));
        });
  }

  private List<String> getPreviousClusterMembersNames(Concept countryConcept) {
    return countryConcept.getSetMembers().stream()
        .map(c -> c.getName().getName())
        .collect(Collectors.toList());
  }

  private List<String> getActualClusterMembersNames(String clusterMembers) {
    return Arrays.stream(clusterMembers.split(",")).map(String::trim).collect(Collectors.toList());
  }

  private void handleClusterMembersChanges(
      Concept countryConcept,
      List<String> previousClusterMembers,
      List<String> actualClusterMembers) {
    handleNewClusterMembers(countryConcept, previousClusterMembers, actualClusterMembers);
    handleChangedClusterMembers(previousClusterMembers, actualClusterMembers);
  }

  private void handleNewClusterMembers(
      Concept countryConcept,
      List<String> previousClusterMembers,
      List<String> actualClusterMembers) {

    actualClusterMembers.stream()
        .filter(
            name -> StringUtils.isNotBlank(name) && !previousClusterMembers.contains(name.trim()))
        .map(name -> countryService.getOrCreateCountryConcept(name))
        .forEach(
            concept -> {
              countryConcept.addSetMember(concept);
              conceptService.saveConcept(countryConcept);
            });
  }

  private void handleChangedClusterMembers(
      List<String> previousClusterMembers, List<String> actualClusterMembers) {

    previousClusterMembers.stream()
        .filter(name -> !actualClusterMembers.contains(name.trim()))
        .map(name -> conceptService.getConceptByName(name))
        .filter(Objects::nonNull)
        .forEach(this::retireConcept);

    actualClusterMembers.stream()
        .filter(
            name ->
                !previousClusterMembers.contains(name.trim())
                    && StringUtils.isNotBlank(name)
                    && conceptService.getConceptByName(name.trim()) == null)
        .forEach(name -> countryService.buildAndSaveCountryResources(name.trim(), null));
  }

  private void retireConcept(Concept concept) {
    concept.setRetired(true);
    concept.setRetiredBy(Context.getAuthenticatedUser());
    concept.setDateRetired(new Date());
    conceptService.saveConcept(concept);
  }

  private void handleCountryNameChange(Concept countryConcept, String countryName) {
    if (isCountryNameChanged(countryConcept, countryName)) {
      ConceptName newName = new ConceptNameBuilder().withName(countryName).build();
      voidOldName(countryConcept.getName());
      countryConcept.setFullySpecifiedName(newName);
      conceptService.saveConcept(countryConcept);
    }
  }

  private boolean isCountryNameChanged(Concept countryConcept, String countryName) {
    String previousCountryName = countryConcept.getName().getName();
    return !StringUtils.equals(previousCountryName, countryName);
  }

  private void handleCountryCodeChange(Concept countryConcept, String countryCode) {
    if (isCountryCodeChanged(countryConcept, countryCode)) {
      ConceptName newShortName =
          new ConceptNameBuilder()
              .withName(countryCode)
              .withConceptNameType(ConceptNameType.SHORT)
              .build();
      voidOldName(countryConcept.getShortNameInLocale(Context.getLocale()));
      countryConcept.setShortName(newShortName);
      conceptService.saveConcept(countryConcept);
    }
  }

  private boolean isCountryCodeChanged(Concept countryConcept, String countryCode) {
    ConceptName previousCountryCode = countryConcept.getShortNameInLocale(Context.getLocale());
    if (previousCountryCode != null) {
      return !StringUtils.equals(previousCountryCode.getName(), countryCode);
    }
    return true;
  }

  private void voidOldName(ConceptName conceptName) {
    if (conceptName != null) {
      conceptName.setVoided(true);
    }
  }

  private String getClusterMembersByCountry(Concept countryConcept) {
    return countryConcept.getSetMembers().stream()
        .filter(concept -> !concept.getRetired())
        .map(concept -> concept.getName().getName())
        .collect(Collectors.joining(", "));
  }

  private ModelAndView createOrUpdateCountryConcept(
      HttpServletRequest httpServletRequest,
      Concept existingCountryConcept,
      CountryControllerModel model) {
    String returnViewName;
    Concept enteredCountryConcept = conceptService.getConceptByName(model.getName());
    List<Concept> countryConcepts = existingCountryConcept.getSetMembers();
    String clusterMembers = model.getClusterMembers();
    String countryCode = model.getCountryCode();
    try {
      if (enteredCountryConcept == null) {
        Concept newCountryConcept =
            countryService.buildAndSaveCountryResources(model.getName(), countryCode);
        addCountryConceptToExistingCountriesList(existingCountryConcept, newCountryConcept);
        countryService.createClusterMembersResources(clusterMembers, newCountryConcept);
        setInfoMessage(httpServletRequest, "cfl.addNewCountry.success.info");
        returnViewName = COUNTRIES_LIST_REDIRECT_VIEW;
      } else if (countryConcepts.contains(enteredCountryConcept)) {
        countryService.createClusterMembersResources(clusterMembers, enteredCountryConcept);
        setInfoMessage(httpServletRequest, "cfl.addNewCountry.alreadyExists");
        returnViewName = COUNTRY_FORM_REDIRECT_VIEW;
      } else {
        addCountryConceptToExistingCountriesList(existingCountryConcept, enteredCountryConcept);
        countryService.createClusterMembersResources(clusterMembers, enteredCountryConcept);
        setInfoMessage(httpServletRequest, "cfl.addNewCountry.success.info");
        returnViewName = COUNTRIES_LIST_REDIRECT_VIEW;
      }
    } catch (Exception ex) {
      setInfoMessage(httpServletRequest, "cfl.addNewCountry.failure.info");
      returnViewName = COUNTRY_FORM_REDIRECT_VIEW;
    }
    return new ModelAndView(new RedirectView(returnViewName));
  }

  private Map<Integer, String> sortCountriesAlphabetically(Map<Integer, String> conceptMap) {
    return conceptMap.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new));
  }

  private void checkDuplicatedCountriesAndSetProperInfoMsg(
      HttpServletRequest httpServletRequest, List<String> duplicatedCountries) {
    if (CollectionUtils.isEmpty(duplicatedCountries)) {
      setInfoMessage(httpServletRequest, "cfl.importCountries.success.info");
    } else {
      setInfoMessage(
          httpServletRequest,
          String.format(
              "The following duplicates were imported only once: %s",
              String.join(", ", duplicatedCountries)));
    }
  }

  private void addCountryConceptToExistingCountriesList(
      Concept sourceConcept, Concept conceptToAdd) {
    sourceConcept.addSetMember(conceptToAdd);
    conceptService.saveConcept(sourceConcept);
  }

  private void setInfoMessage(HttpServletRequest request, String message) {
    request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, message);
  }
}
