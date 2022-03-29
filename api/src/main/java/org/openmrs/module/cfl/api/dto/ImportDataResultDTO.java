package org.openmrs.module.cfl.api.dto;

import java.util.List;

/** ImportDataResultDTO class - represents result data after importing file with addresses */
public class ImportDataResultDTO {

  int numberOfTotalRecords;

  int numberOfInvalidRecords;

  List<String> invalidRecords;

  public ImportDataResultDTO() {}

  public ImportDataResultDTO(
      int numberOfTotalRecords, int numberOfInvalidRecords, List<String> invalidRecords) {
    this.numberOfTotalRecords = numberOfTotalRecords;
    this.numberOfInvalidRecords = numberOfInvalidRecords;
    this.invalidRecords = invalidRecords;
  }

  public int getNumberOfTotalRecords() {
    return numberOfTotalRecords;
  }

  public void setNumberOfTotalRecords(int numberOfTotalRecords) {
    this.numberOfTotalRecords = numberOfTotalRecords;
  }

  public int getNumberOfInvalidRecords() {
    return numberOfInvalidRecords;
  }

  public void setNumberOfInvalidRecords(int numberOfInvalidRecords) {
    this.numberOfInvalidRecords = numberOfInvalidRecords;
  }

  public List<String> getInvalidRecords() {
    return invalidRecords;
  }

  public void setInvalidRecords(List<String> invalidRecords) {
    this.invalidRecords = invalidRecords;
  }
}
