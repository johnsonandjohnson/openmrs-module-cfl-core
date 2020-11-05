package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.IrisLocationService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class IrisLocationServiceImpl extends BaseOpenmrsService implements IrisLocationService {

    private LocationService locationService;

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Location saveLocation(Location location) {
        return locationService.saveLocation(location);
    }
}
