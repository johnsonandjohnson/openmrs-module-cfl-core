package org.openmrs.module.cfl.page.requestmapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.util.GlobalPropertyUtils;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.page.PageRequestMapper;
import org.springframework.stereotype.Component;

import static org.openmrs.module.cfl.CFLConstants.PATIENT_DASHBOARD_ATTR_VALUE;
import static org.openmrs.module.cfl.CFLConstants.PERSON_DASHBOARD_ATTR_VALUE;

@Component
public class ClinicianfacingPageRequestMapper implements PageRequestMapper {

    private static final Log LOGGER = LogFactory.getLog(ClinicianfacingPageRequestMapper.class);

    private static final String CFL_PROVIDER_NAME = CFLConstants.MODULE_ID;
    private static final String PERSON_PAGE_NAME = "person";

    private static final String COREAPPS_PROVIDER_NAME = "coreapps";
    private static final String PATIENT_PAGE_NAME = "clinicianfacing/patient";

    private static final String CUSTOM_DASHBOARD_ATTR_NAME = "dashboard";

    @Override
    public boolean mapRequest(PageRequest request) {
        if (isCoreappsPatientOrPersonDashboard(request) && isRedirectingToPersonDashboardEnabled()) {
            LOGGER.info(String.format(
                    "The redirection to person dashboard is enabled - redirecting FROM %s.%s TO %s.%s",
                    COREAPPS_PROVIDER_NAME, PATIENT_PAGE_NAME,
                    CFL_PROVIDER_NAME, PERSON_PAGE_NAME));
            request.setProviderNameOverride(CFLConstants.MODULE_ID);
            request.setPageNameOverride(PERSON_PAGE_NAME);
            return true;
        }
        return false;
    }

    private boolean isRedirectingToPersonDashboardEnabled() {
        return GlobalPropertyUtils.isTrue(CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME);
    }

    private boolean isCoreappsPatientOrPersonDashboard(PageRequest request) {
        return request.getProviderName().equals(COREAPPS_PROVIDER_NAME)
                && request.getPageName().equals(PATIENT_PAGE_NAME)
                && areAttributesPointsAtPatientOrPersonDashboard(request);
    }

    private boolean areAttributesPointsAtPatientOrPersonDashboard(PageRequest request) {
        String dashboardAttr = (String) request.getAttribute(CUSTOM_DASHBOARD_ATTR_NAME);
        boolean isPatientDashboard = dashboardAttr == null
                || StringUtils.equals(dashboardAttr, PATIENT_DASHBOARD_ATTR_VALUE);
        boolean isPersonDashboard = StringUtils.equals(dashboardAttr, PERSON_DASHBOARD_ATTR_VALUE);
        return isPatientDashboard || isPersonDashboard;
    }
}
