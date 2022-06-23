/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.builder;

import org.openmrs.module.cflcore.api.model.GlobalPropertyHistory;
import org.openmrs.module.cflcore.api.util.DateUtil;

import java.util.Date;

public class GlobalPropertyHistoryBuilder extends AbstractBuilder<GlobalPropertyHistory> {

    private Integer id;

    private String action;

    private Date actionDate;

    private String property;

    private String value;

    private String description;

    public GlobalPropertyHistoryBuilder() {
        super();
        id = getInstanceNumber();
        action = "UPDATE";
        actionDate = DateUtil.now();
        property = "Test GP name";
        value = "Test GP value";
        description = "Test GP description";
    }

    @Override
    public GlobalPropertyHistory build() {
        GlobalPropertyHistory globalPropertyHistory = new GlobalPropertyHistory();
        globalPropertyHistory.setId(id);
        globalPropertyHistory.setAction(action);
        globalPropertyHistory.setActionDate(actionDate);
        globalPropertyHistory.setProperty(property);
        globalPropertyHistory.setPropertyValue(value);
        globalPropertyHistory.setDescription(description);
        return globalPropertyHistory;
    }

    @Override
    public GlobalPropertyHistory buildAsNew() {
        return null;
    }

    public GlobalPropertyHistoryBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public GlobalPropertyHistoryBuilder withAction(String action) {
        this.action = action;
        return this;
    }

    public GlobalPropertyHistoryBuilder withActionDate(Date actionDate) {
        this.actionDate = actionDate;
        return this;
    }

    public GlobalPropertyHistoryBuilder withProperty(String property) {
        this.property = property;
        return this;
    }

    public GlobalPropertyHistoryBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public GlobalPropertyHistoryBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
}
