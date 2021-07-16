package org.openmrs.module.cfl.web.model;

import org.openmrs.module.messages.domain.PagingInfo;

public final class PageableParams {

    private Integer rows;

    private Integer page;

    public PagingInfo getPagingInfo(int allRecordsPageSize) {
        validatePageIndex(page);
        validatePageSize(rows);

        PagingInfo pagingInfo;
        if (hasPageInfo()) {
            pagingInfo = new PagingInfo(page, rows);
        } else {
            pagingInfo = new PagingInfo(1, allRecordsPageSize);
        }

        return pagingInfo;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    private boolean hasPageInfo() {
        return getPage() != null || getRows() != null;
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

