package org.openmrs.module.cfl.api.model;

import java.util.List;

/**
 * AddressDataContent class - represents results data used in {@link org.openmrs.module.cfl.api.dto.AddressDataDTO}
 */
public class AddressDataContent {

    private List<String> results;

    public AddressDataContent(List<String> results) {
        this.results = results;
    }

    public List<String> getContent() {
        return results;
    }

    public void setContent(List<String> results) {
        this.results = results;
    }
}
