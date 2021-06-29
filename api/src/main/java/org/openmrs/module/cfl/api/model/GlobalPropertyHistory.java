package org.openmrs.module.cfl.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity(name = "cfl.GlobalPropertyHistory")
@Table(name = "global_property_history")
public class GlobalPropertyHistory {

    @Id
    @Column(name = "global_property_history_id")
    private Integer id;

    @Column(name = "action")
    private String action;

    @Column(name = "action_date")
    private Date actionDate;

    @Column(name = "property", nullable = false)
    private String property;

    @Column(name = "property_value", columnDefinition = "TEXT")
    private String propertyValue;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "datatype")
    private String datatype;

    @Column(name = "datatype_config", columnDefinition = "TEXT")
    private String datatypeConfig;

    @Column(name = "preferred_handler")
    private String preferredHandler;

    @Column(name = "handler_config", columnDefinition = "TEXT")
    private String handlerConfig;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getDatatypeConfig() {
        return datatypeConfig;
    }

    public void setDatatypeConfig(String datatypeConfig) {
        this.datatypeConfig = datatypeConfig;
    }

    public String getPreferredHandler() {
        return preferredHandler;
    }

    public void setPreferredHandler(String preferredHandler) {
        this.preferredHandler = preferredHandler;
    }

    public String getHandlerConfig() {
        return handlerConfig;
    }

    public void setHandlerConfig(String handlerConfig) {
        this.handlerConfig = handlerConfig;
    }
}
