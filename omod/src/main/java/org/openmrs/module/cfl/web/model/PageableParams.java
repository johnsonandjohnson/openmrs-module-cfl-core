package org.openmrs.module.cfl.web.model;

import org.openmrs.module.messages.domain.PagingInfo;

public final class PageableParams {

    private Integer rows;

    private Integer page;

    public PagingInfo getPagingInfo() {
        validatePageIndex(page);
        validatePageSize(rows);

        return new PagingInfo(page, rows);
    }

    public Integer getRows() {
        return rows;
    }

    public PageableParams setRows(Integer rows) {
        this.rows = rows;
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public boolean hasPageInfo() {
        return getPage() != null || getRows() != null;
    }

    public PageableParams setPage(Integer page) {
        this.page = page;
        return this;
    }

    private void validatePageIndex(Integer pageNumber) {
        if (pageNumber != null && pageNumber <= 0) {
            throw new IllegalArgumentException(String.format("Invalid page number %s", pageNumber));
        }
    }

    private void validatePageSize(Integer pageSize) {
        if (pageSize != null && pageSize <= 0) {
            throw new IllegalArgumentException(String.format("Invalid page size %s", pageSize));
        }
    }
}

