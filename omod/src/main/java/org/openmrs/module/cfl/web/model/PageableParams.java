/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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

