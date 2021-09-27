package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cfl.api.service.IrisAdminService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class IrisAdminServiceImpl extends BaseOpenmrsService implements IrisAdminService {

    private AdministrationService adminService;

    public void setAdminService(AdministrationService adminService) {
        this.adminService = adminService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setGlobalProperty(String key, String val) {
        adminService.setGlobalProperty(key, val);
    }
}
