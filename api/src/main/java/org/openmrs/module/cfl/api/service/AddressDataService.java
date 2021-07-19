package org.openmrs.module.cfl.api.service;

import org.openmrs.module.cfl.api.model.AddressDataContent;

import java.util.List;

/**
 * AddressDataService interface which provides methods for address data functionalities.
 */
public interface AddressDataService {

    List<AddressDataContent> getAddressDataResults();
}
