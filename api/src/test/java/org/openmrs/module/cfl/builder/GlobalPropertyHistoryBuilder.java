package org.openmrs.module.cfl.builder;

import org.openmrs.module.cfl.api.model.GlobalPropertyHistory;
import org.openmrs.module.cfl.api.util.DateUtil;

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
