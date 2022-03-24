package org.openmrs.module.cfl.api.dto;

import org.openmrs.module.cfl.api.model.AddressDataContent;

import java.util.List;

/** The AddressDataDTO class - represents required data for Address Data UI. */
public class AddressDataDTO {

  private int pageNumber;

  private int pageSize;

  private int totalCount;

  private boolean isNextPage;

  private List<AddressDataContent> content;

  public AddressDataDTO() {}

  public AddressDataDTO(
      int pageNumber,
      int pageSize,
      int totalCount,
      boolean isNextPage,
      List<AddressDataContent> content) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.totalCount = totalCount;
    this.isNextPage = isNextPage;
    this.content = content;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public AddressDataDTO setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }

  public boolean isNextPage() {
    return isNextPage;
  }

  public void setIsNextPage(boolean isNextPage) {
    this.isNextPage = isNextPage;
  }

  public List<AddressDataContent> getContent() {
    return content;
  }

  public void setContent(List<AddressDataContent> content) {
    this.content = content;
  }
}
