/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.cflcore.api.dto.AddressDataDTO;
import org.openmrs.module.cflcore.api.dto.ImportDataResultDTO;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.cflcore.api.service.CFLAddressHierarchyService;
import org.openmrs.module.cflcore.db.ExtendedAddressHierarchyDAO;
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
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.Objects;
import java.util.stream.Collectors;

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

  @Transactional(propagation = Propagation.REQUIRED)
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
        ValidatorResult validatedLine =
                new AddressDataValidator(line, delimiter, alreadyProcessedLines).validate();

        if (isValidLine(validatedLine.getErrorMessage())) {
          processLine(
                  validatedLine.getLine(), entriesToCreate, levels, levelsEntriesMap, overwriteData);
        } else {
          failedRecords.add(validatedLine.getErrorMessage());
          LOGGER.warn("Line: {} is duplicated or has an invalid format", line);
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

      AddressHierarchyEntry parent = getFieldParent(entriesToCreate, levels, splitFields, i, levelsEntriesMap, overwriteData);

      if(isHierarchyEntryAlreadyExists(listToSearchCurrentEntry, currentField, currentLevel, parent)) {
        continue;
      }

      entriesToCreate.add(buildAddressHierarchyEntry(currentField, currentLevel, parent));
    }
  }

  private AddressHierarchyEntry getFieldParent(
          List<AddressHierarchyEntry> entriesToCreate,
          List<AddressHierarchyLevel> levels,
          List<String> splitFields,
          int fieldIndex,
          Map<AddressHierarchyLevel, List<AddressHierarchyEntry>> levelsEntriesMap,
          boolean overwriteData){
    AddressHierarchyEntry parent = null;
    if (fieldIndex > 0) {
      List<AddressHierarchyEntry> listToSearchParentEntry = new ArrayList<>(entriesToCreate);
      AddressHierarchyLevel parentLevel = levels.get(fieldIndex - 1);
      if (!overwriteData) {
        listToSearchParentEntry.addAll(levelsEntriesMap.get(parentLevel));
      }
      parent =
              findParentAddressHierarchyEntry(
                      listToSearchParentEntry, splitFields.get(fieldIndex - 1), parentLevel, splitFields.subList(0, fieldIndex - 1));
    }
    return parent;
  }

  private AddressHierarchyEntry findParentAddressHierarchyEntry(
          List<AddressHierarchyEntry> entriesToCreate, String field, AddressHierarchyLevel level, List<String> currentParentList) {
    List<AddressHierarchyEntry> addressHierarchyEntryList = new ArrayList<>();
    addressHierarchyEntryList = entriesToCreate.stream()
            .filter(entry -> StringUtils.equalsIgnoreCase(entry.getName(), field))
            .filter(entry -> Objects.equals(entry.getLevel(), level))
            .filter(entry -> checkParents(entry, currentParentList))
            .collect(Collectors.toList());
    if (addressHierarchyEntryList.size() > 1) {
      throw new CflRuntimeException("Ambiguous parent for " + currentParentList + " Field::" + field);
    }
    return addressHierarchyEntryList.isEmpty() ? null : addressHierarchyEntryList.get(0);
  }

  private boolean checkParents(AddressHierarchyEntry entry, List<String> currentParentList){
    AddressHierarchyEntry currentParent= entry.getParent();

    for (int i = currentParentList.size() - 1; i >= 0; --i) {
      if(! currentParent.getName().equals(currentParentList.get(i)) ) {
        return false;
      }
      currentParent = currentParent.getParent();
    }
    return true;
  }

  private boolean isHierarchyEntryAlreadyExists(
          List<AddressHierarchyEntry> listToSearchCurrentEntry, String field, AddressHierarchyLevel level, AddressHierarchyEntry parent) {
    return listToSearchCurrentEntry.stream()
            .filter(entry -> StringUtils.equalsIgnoreCase(entry.getName(), field))
            .filter(entry -> Objects.equals(entry.getParent(), parent))
            .anyMatch(entry -> Objects.equals(entry.getLevel(), level));
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
  static class AddressDataValidator {
    private final String rawLine;
    private final String delimiter;
    private final Set<String> processedLines;
    private final List<String> splitFieldsWithEmptyTrim;

    AddressDataValidator(String rawLine, String delimiter, List<String> processedLines) {
      this.rawLine = rawLine;
      this.delimiter = delimiter;
      this.processedLines = new HashSet<>(processedLines);
      this.splitFieldsWithEmptyTrim = getSplitFieldsWithEmptyTrim(rawLine, delimiter);
    }

    private static List<String> getSplitFieldsWithEmptyTrim(String rawLine, String delimiter) {
      final List<String> splitLine = new ArrayList<>(Arrays.asList(rawLine.split(delimiter, -1)));

      final ListIterator<String> splitLineIterator = splitLine.listIterator(splitLine.size());
      while (splitLineIterator.hasPrevious()) {
        final String currentCell = splitLineIterator.previous();

        if (StringUtils.isBlank(currentCell)) {
          splitLineIterator.remove();
        } else {
          break;
        }
      }

      return splitLine;
    }

    ValidatorResult validate() {
      StringJoiner joiner = new StringJoiner(" ", rawLine + delimiter + "-> Reason: ", "");
      joiner.setEmptyValue("");

      if (splitFieldsWithEmptyTrim.size() > NUMBER_OF_ADDRESS_HIERARCHY_LEVELS) {
        joiner.add(
                "Too much fields. The allowed number of fields is "
                        + NUMBER_OF_ADDRESS_HIERARCHY_LEVELS
                        + ".");
      }

      if (splitFieldsWithEmptyTrim.stream().anyMatch(StringUtils::isBlank)) {
        joiner.add("The line has empty cells.");
      }

      if (processedLines.contains(rawLine)) {
        joiner.add("Duplicated record.");
      }

      return new ValidatorResult(splitFieldsWithEmptyTrim, joiner.toString());
    }
  }

  static class ValidatorResult {
    final List<String> line;
    final String errorMessage;

    ValidatorResult(List<String> line, String errorMessage) {
      this.line = line;
      this.errorMessage = errorMessage;
    }

    List<String> getLine() {
      return line;
    }

    String getErrorMessage() {
      return errorMessage;
    }
  }
}
