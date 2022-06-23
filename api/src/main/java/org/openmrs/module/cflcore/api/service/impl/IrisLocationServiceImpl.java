/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.api.LocationService;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cflcore.api.service.IrisLocationService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class IrisLocationServiceImpl extends BaseOpenmrsService implements IrisLocationService {

    private static final Log LOGGER = LogFactory.getLog(IrisLocationServiceImpl.class);

    private LocationService locationService;
    private LocationDAO locationDAO;

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    public void setLocationDAO(LocationDAO locationDAO) {
        this.locationDAO = locationDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Location saveLocation(Location location) {
        return locationService.saveLocation(location);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Location updateLocation(Location location) {
        LOGGER.info(String.format("In method IrisLocationService.updateLocation. Arguments: %s", location.toString()));

        Location old = locationDAO.getLocation(location.getName());
        old.setTags(location.getTags());
        old.setCountry(location.getCountry());
        old.setCityVillage(location.getCityVillage());
        for (LocationAttribute locationAttribute :  location.getAttributes()) {
            old.setAttribute(locationAttribute);
        }
        LOGGER.info("Exiting method updateLocation");

        return locationDAO.saveLocation(old);
    }
}
