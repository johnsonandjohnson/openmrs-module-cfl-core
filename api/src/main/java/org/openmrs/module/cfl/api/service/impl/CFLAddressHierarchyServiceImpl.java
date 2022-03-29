package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.cfl.api.dto.AddressDataDTO;
import org.openmrs.module.cfl.api.dto.ImportDataResultDTO;
import org.openmrs.module.cfl.api.service.CFLAddressHierarchyService;
import org.openmrs.module.cfl.db.ExtendedAddressHierarchyDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
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
import java.util.StringJoiner;

/** The default implementation of CFLAddressHierarchyService. */
public class CFLAddressHierarchyServiceImpl extends BaseOpenmrsService
    implements CFLAddressHierarchyService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CFLAddressHierarchyServiceImpl.class);

  private static final Integer NUMBER_OF_ADDRESS_HIERARCHY_LEVELS = 5;

  private static final Integer BATCH_SIZE = 100;

  private static final AddressField[] ADDRESS_FIELDS = {
    AddressField.COUNTRY,
    AddressField.STATE_PROVINCE,
    AddressField.COUNTY_DISTRICT,
    AddressField.CITY_VILLAGE,
    AddressField.POSTAL_CODE
  };

  private ExtendedAddressHierarchyDAO extendedAddressHierarchyDAO;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void safeDeleteAllAddressHierarchyEntries() {
    extendedAddressHierarchyDAO.deleteAllAddressHierarchyEntriesSafely();
    // Extract from context, because bean in not named in address hierarchy
    Context.getService(AddressHierarchyService.class).resetFullAddressCache();
  }

  @Override
  public ImportDataResultDTO importAddressHierarchyEntriesAndReturnInvalidRows(
      InputStream inputStream, String delimiter, boolean overwriteData) throws IOException {
    if (overwriteData) {
      clearOldData();
    }

    return createAddressHierarchyEntriesAndReturnDuplicatedRows(
        inputStream, delimiter, overwriteData);
  }

  @Transactional(readOnly = true)
  @Override
  public AddressDataDTO getAddressDataResults(Integer pageNumber, Integer pageSize) {
    return extendedAddressHierarchyDAO.getAddressData(pageNumber, pageSize);
  }

  @Override
  @Transactional(readOnly = true)
  public int getCountAllAddressData() {
    return extendedAddressHierarchyDAO.getCountAllAddressData();
  }

  @Override
  public void createAddressHierarchyLevels() {
    List<AddressHierarchyLevel> levels = new ArrayList<>();
    AddressHierarchyService addressHierarchyService =
        Context.getService(AddressHierarchyService.class);
    for (int i = 0; i < NUMBER_OF_ADDRESS_HIERARCHY_LEVELS; i++) {
      AddressHierarchyLevel parent = null;
      if (i != 0) {
        parent = levels.get(i - 1);
      }
      AddressHierarchyLevel level = buildAddressHierarchyLevel(parent, ADDRESS_FIELDS[i]);
      addressHierarchyService.saveAddressHierarchyLevel(level);
      levels.add(level);
    }
  }

  @Override
  public void deleteAllAddressHierarchyLevels() {
    extendedAddressHierarchyDAO.deleteAllAddressHierarchyLevels();
  }

  @Override
  public void recreateAddressHierarchyLevels() {
    deleteAllAddressHierarchyLevels();
    createAddressHierarchyLevels();
  }

  @Override
  public void saveAddressHierarchyEntriesInBatches(List<AddressHierarchyEntry> entriesToCreate) {
    int numberOfIterations = (int) Math.ceil((float) entriesToCreate.size() / BATCH_SIZE);
    AddressHierarchyService addressHierarchyService =
        Context.getService(AddressHierarchyService.class);
    for (int i = 0; i < numberOfIterations; i++) {
      int endIndex = Math.min((i + 1) * BATCH_SIZE, entriesToCreate.size());
      List<AddressHierarchyEntry> subList = entriesToCreate.subList(i * BATCH_SIZE, endIndex);
      addressHierarchyService.saveAddressHierarchyEntries(subList);
    }
  }

  public void setExtendedAddressHierarchyDAO(
      ExtendedAddressHierarchyDAO extendedAddressHierarchyDAO) {
    this.extendedAddressHierarchyDAO = extendedAddressHierarchyDAO;
  }

  private void clearOldData() {
    extendedAddressHierarchyDAO.deleteAllAddressHierarchyEntriesSafely();
    recreateAddressHierarchyLevels();
  }

  private ImportDataResultDTO createAddressHierarchyEntriesAndReturnDuplicatedRows(
      InputStream inputStream, String delimiter, boolean overwriteData) throws IOException {
    List<String> failedRecords = new ArrayList<>();
    List<AddressHierarchyEntry> entriesToCreate = new ArrayList<>();
    List<AddressHierarchyLevel> levels =
        Context.getService(AddressHierarchyService.class).getAddressHierarchyLevels();

    Map<AddressHierarchyLevel, List<AddressHierarchyEntry>> levelsEntriesMap =
        getAddressLevelsEntriesMap(levels);

    List<String> alreadyProcessedLines = new ArrayList<>();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String lineToValidate =
            new AddressDataValidator(line, delimiter, alreadyProcessedLines)
                .getErrorMessageOfValidatedLine();
        if (isValidLine(lineToValidate)) {
          List<String> splitFields = Arrays.asList(line.split(delimiter, -1));
          processLine(splitFields, entriesToCreate, levels, levelsEntriesMap, overwriteData);
        } else {
          failedRecords.add(lineToValidate);
          LOGGER.warn(String.format("Line: %s is duplicated or has an invalid format", line));
        }
        alreadyProcessedLines.add(line);
      }
    }

    saveAddressHierarchyEntriesInBatches(entriesToCreate);

    return new ImportDataResultDTO(
        alreadyProcessedLines.size(), failedRecords.size(), failedRecords);
  }

  private boolean isValidLine(String line) {
    return StringUtils.isBlank(line);
  }

  private Map<AddressHierarchyLevel, List<AddressHierarchyEntry>> getAddressLevelsEntriesMap(
      List<AddressHierarchyLevel> levels) {
    AddressHierarchyService addressHierarchyService =
        Context.getService(AddressHierarchyService.class);
    Map<AddressHierarchyLevel, List<AddressHierarchyEntry>> levelsEntriesMap = new HashMap<>();
    levels.forEach(
        level ->
            levelsEntriesMap.put(
                level, addressHierarchyService.getAddressHierarchyEntriesByLevel(level)));

    return levelsEntriesMap;
  }

  private void processLine(
      List<String> splitFields,
      List<AddressHierarchyEntry> entriesToCreate,
      List<AddressHierarchyLevel> levels,
      Map<AddressHierarchyLevel, List<AddressHierarchyEntry>> levelsEntriesMap,
      boolean overwriteData) {
    for (int i = 0; i < splitFields.size(); i++) {
      String currentField = splitFields.get(i);
      AddressHierarchyLevel currentLevel = levels.get(i);
      List<AddressHierarchyEntry> listToSearchCurrentEntry = new ArrayList<>(entriesToCreate);

      if (!overwriteData) {
        listToSearchCurrentEntry.addAll(levelsEntriesMap.get(levels.get(i)));
      }

      if (isHierarchyEntryAlreadyExists(listToSearchCurrentEntry, currentField, currentLevel)) {
        continue;
      }

      AddressHierarchyEntry parent = null;
      if (i > 0) {
        List<AddressHierarchyEntry> listToSearchParentEntry = new ArrayList<>(entriesToCreate);
        AddressHierarchyLevel parentLevel = levels.get(i - 1);
        if (!overwriteData) {
          listToSearchParentEntry.addAll(levelsEntriesMap.get(parentLevel));
        }
        parent =
            findAddressHierarchyByNameAndLevel(
                listToSearchParentEntry, splitFields.get(i - 1), parentLevel);
      }

      entriesToCreate.add(buildAddressHierarchyEntry(currentField, currentLevel, parent));
    }
  }

  private AddressHierarchyEntry findAddressHierarchyByNameAndLevel(
      List<AddressHierarchyEntry> entriesToCreate, String field, AddressHierarchyLevel level) {
    return entriesToCreate.stream()
        .filter(entry -> StringUtils.equalsIgnoreCase(entry.getName(), field))
        .filter(entry -> entry.getLevel().equals(level))
        .findFirst()
        .orElse(null);
  }

  private boolean isHierarchyEntryAlreadyExists(
      List<AddressHierarchyEntry> entriesToCreate, String field, AddressHierarchyLevel level) {
    return entriesToCreate.stream()
        .filter(entry -> StringUtils.equalsIgnoreCase(entry.getName(), field))
        .anyMatch(entry -> entry.getLevel().equals(level));
  }

  private AddressHierarchyEntry buildAddressHierarchyEntry(
      String name, AddressHierarchyLevel level, AddressHierarchyEntry parent) {
    AddressHierarchyEntry addressHierarchyEntry = new AddressHierarchyEntry();
    addressHierarchyEntry.setName(name);
    addressHierarchyEntry.setLevel(level);
    addressHierarchyEntry.setParent(parent);

    return addressHierarchyEntry;
  }

  private AddressHierarchyLevel buildAddressHierarchyLevel(
      AddressHierarchyLevel parent, AddressField addressField) {
    AddressHierarchyLevel addressHierarchyLevel = new AddressHierarchyLevel();
    addressHierarchyLevel.setParent(parent);
    addressHierarchyLevel.setAddressField(addressField);

    return addressHierarchyLevel;
  }

  /** Internal helper class to validate lines obtained from file */
  private static class AddressDataValidator {
    private String line;
    private String delimiter;
    private List<String> processedLines;

    public AddressDataValidator(String line, String delimiter, List<String> processedLines) {
      this.line = line;
      this.delimiter = delimiter;
      this.processedLines = processedLines;
    }

    private String getErrorMessageOfValidatedLine() {
      StringJoiner joiner = new StringJoiner(" ", line + delimiter + "-> Reason: ", "");
      joiner.setEmptyValue("");
      List<String> splitFields = Arrays.asList(line.split(delimiter, -1));

      if (splitFields.size() > NUMBER_OF_ADDRESS_HIERARCHY_LEVELS) {
        joiner.add(
            "Too much fields. The allowed number of fields is "
                + NUMBER_OF_ADDRESS_HIERARCHY_LEVELS
                + ".");
      }

      if (splitFields.stream().anyMatch(StringUtils::isBlank)) {
        joiner.add("The line has empty cells.");
      }

      if (processedLines.contains(line)) {
        joiner.add("Duplicated record.");
      }

      return joiner.toString();
    }
  }
}
