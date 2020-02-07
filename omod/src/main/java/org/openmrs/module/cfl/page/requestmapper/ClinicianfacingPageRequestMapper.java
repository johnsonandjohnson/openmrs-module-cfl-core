package org.openmrs.module.cfl.page.requestmapper;

import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.util.GlobalPropertyUtils;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.page.PageRequestMapper;
import org.springframework.stereotype.Component;

@Component
public class ClinicianfacingPageRequestMapper implements PageRequestMapper {

    @Override
    public boolean mapRequest(PageRequest request) {
        if (isCoreappsPatientDashboard(request) && isRedirectingToPersonDashboardEnabled()) {
            request.setProviderNameOverride(CFLConstants.MODULE_ID);
            request.setPageNameOverride("person");
            return true;
        }
        return false;
    }

    private boolean isRedirectingToPersonDashboardEnabled() {
        return GlobalPropertyUtils.isTrue(CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME);
    }

    private boolean isCoreappsPatientDashboard(PageRequest request) {
        return request.getProviderName().equals("coreapps") && request.getPageName().equals("clinicianfacing/patient");
    }
}
