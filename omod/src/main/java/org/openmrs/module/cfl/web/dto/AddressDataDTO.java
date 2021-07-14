package org.openmrs.module.cfl.web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * The AddressDataDTO class - represents required data for Address Data UI.
 */
public class AddressDataDTO {

    private int pageNumber;

    private int pageSize;

    private int totalCount;

    private boolean isNextPage;

    private List<List<String>> content = new ArrayList<>();

    public AddressDataDTO() {

    }

    public AddressDataDTO(int pageNumber, int pageSize, int totalCount, boolean isNextPage,
                          List<List<String>> content) {
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

    public AddressDataDTO setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public AddressDataDTO setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public boolean isNextPage() {
        return isNextPage;
    }

    public AddressDataDTO setIsNextPage(boolean isNextPage) {
        this.isNextPage = isNextPage;
        return this;
    }

    public List<List<String>> getContent() {
        return content;
    }

    public AddressDataDTO setContent(List<List<String>> content) {
        this.content = content;
        return this;
    }
}
