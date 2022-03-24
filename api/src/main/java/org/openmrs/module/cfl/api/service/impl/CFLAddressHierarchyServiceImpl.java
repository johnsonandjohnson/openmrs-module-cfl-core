package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.cfl.api.dto.AddressDataDTO;
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

/** The default implementation of CFLAddressHierarchyService. */
public class CFLAddressHierarchyServiceImpl extends BaseOpenmrsService
    implements CFLAddressHierarchyService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CFLAddressHierarchyServiceImpl.class);

  private static final Integer NUMBER_OF_ADDRESS_HIERARCHY_LEVELS = 5;

  private static final Integer BATCH_SIZE = 100;

  private ExtendedAddressHierarchyDAO extendedAddressHierarchyDAO;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void safeDeleteAllAddressHierarchyEntries() {
    extendedAddressHierarchyDAO.deleteAllAddressHierarchyEntriesSafely();
    // Extract from context, because bean in not named in address hierarchy
    Context.getService(AddressHierarchyService.class).resetFullAddressCache();
  }

  @Override
  public List<String> importAddressHierarchyEntriesAndReturnInvalidRows(
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
      AddressHierarchyLevel level = buildAddressHierarchyLevel(parent);
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

  private void clearOldData() {
    Context.getService(AddressHierarchyService.class).deleteAllAddressHierarchyEntries();
    Context.getService(CFLAddressHierarchyService.class).recreateAddressHierarchyLevels();
  }

  private List<String> createAddressHierarchyEntriesAndReturnDuplicatedRows(
      InputStream inputStream, String delimiter, boolean overwriteData) throws IOException {
    List<String> failedRows = new ArrayList<>();
    List<AddressHierarchyEntry> entriesToCreate = new ArrayList<>();
    List<AddressHierarchyLevel> levels =
        Context.getService(AddressHierarchyService.class).getAddressHierarchyLevels();

    Map<AddressHierarchyLevel, List<AddressHierarchyEntry>> levelsEntriesMap =
        getAddressLevelsEntriesMap(levels);

    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      String line;
      while (StringUtils.isNotBlank(line = reader.readLine())) {
        List<String> splitFields = Arrays.asList(line.split(delimiter, -1));
        if (isValidLine(splitFields)) {
          processLine(splitFields, entriesToCreate, levels, levelsEntriesMap, overwriteData);
        } else {
          failedRows.add(line);
          LOGGER.warn(String.format("Line: %s has an invalid format", line));
        }
      }
    }

    saveAddressHierarchyEntriesInBatches(entriesToCreate);

    return failedRows;
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

      List<AddressHierarchyEntry> listToSearch = new ArrayList<>(entriesToCreate);
      AddressHierarchyEntry parent = null;
      if (i > 0) {
        if (!overwriteData) {
          listToSearch.addAll(levelsEntriesMap.get(levels.get(i - 1)));
        }
        parent =
            findAddressHierarchyByNameAndLevel(
                listToSearch, splitFields.get(i - 1), levels.get(i - 1));
      }

      if (isHierarchyEntryAlreadyExists(listToSearch, currentField, currentLevel)) {
        continue;
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

  private boolean isValidLine(List<String> splitFields) {
    return splitFields.size() <= NUMBER_OF_ADDRESS_HIERARCHY_LEVELS
        && splitFields.stream().noneMatch(StringUtils::isBlank);
  }

  private AddressHierarchyLevel buildAddressHierarchyLevel(AddressHierarchyLevel parent) {
    AddressHierarchyLevel addressHierarchyLevel = new AddressHierarchyLevel();
    addressHierarchyLevel.setParent(parent);

    return addressHierarchyLevel;
  }

  public void setExtendedAddressHierarchyDAO(
      ExtendedAddressHierarchyDAO extendedAddressHierarchyDAO) {
    this.extendedAddressHierarchyDAO = extendedAddressHierarchyDAO;
  }
}
