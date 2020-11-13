package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.api.LocationService;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.IrisLocationService;
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
