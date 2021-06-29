package org.openmrs.module.cfl.api.service;

/**
 * Provides methods for management of {@link org.openmrs.module.cfl.api.model.GlobalPropertyHistory} entity.
 */
public interface GlobalPropertyHistoryService {

    /**
     * Gets the latest value of particular global property. It is retrieved from global_property_history table.
     *
     * @param gpName - the name of global property (property column)
     * @return the latest string value of given global property
     */
    String getLastValueOfGlobalProperty(String gpName);
}
