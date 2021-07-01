package org.openmrs.module.cfl.api.service;

import org.openmrs.module.cfl.api.model.GlobalPropertyHistory;

/**
 * Provides methods for management of {@link org.openmrs.module.cfl.api.model.GlobalPropertyHistory} entity.
 */
public interface GlobalPropertyHistoryService {

    /**
     * Gets the last value of particular global property. It is retrieved from global_property_history table.
     *
     * @param gpName - the name of global property (property column)
     * @return - the last string value of given global property
     */
    GlobalPropertyHistory getLastValueOfGlobalProperty(String gpName);
}
