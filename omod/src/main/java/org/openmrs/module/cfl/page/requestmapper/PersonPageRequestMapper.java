package org.openmrs.module.cfl.page.requestmapper;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.page.PageRequestMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonPageRequestMapper implements PageRequestMapper {

    @Override
    public boolean mapRequest(PageRequest request) {
        if (isCoreappsPatientDashboard(request) && isRedirectingToPersonDashboardEnabled()) {
            request.setProviderNameOverride("cfl");
            request.setPageNameOverride("person");
            return true;
        }
        return false;
    }

    private boolean isRedirectingToPersonDashboardEnabled() {
        String gp = Context.getAdministrationService()
                .getGlobalProperty(CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME);
        return StringUtils.isNotBlank(gp) && Boolean.valueOf(gp);
    }

    private boolean isCoreappsPatientDashboard(PageRequest request) {
        return request.getProviderName().equals("coreapps") && request.getPageName().equals("clinicianfacing/patient");
    }
}
