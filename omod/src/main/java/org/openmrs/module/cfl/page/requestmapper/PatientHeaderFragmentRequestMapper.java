package org.openmrs.module.cfl.page.requestmapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.util.GlobalPropertyUtils;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.openmrs.ui.framework.fragment.FragmentRequestMapper;
import org.springframework.stereotype.Component;

@Component
public class PatientHeaderFragmentRequestMapper implements FragmentRequestMapper {

    private static final Log LOGGER = LogFactory.getLog(PatientHeaderFragmentRequestMapper.class);
    private static final String COREAPPS_PROVIDER_NAME = "coreapps";
    private static final String PATIENT_HEADER_FRAGMENT_ID = "patientHeader";
    private static final String CFL_PROVIDER_NAME = CFLConstants.MODULE_ID;

    @Override
    public boolean mapRequest(FragmentRequest request) {
        if (isCoreappsPatientHeader(request) && isRedirectingToPersonDashboardEnabled()) {
            LOGGER.info(String.format(
                    "The redirection to patient header is enabled - redirecting FROM %s.%s TO %s.%s",
                    COREAPPS_PROVIDER_NAME, PATIENT_HEADER_FRAGMENT_ID,
                    CFL_PROVIDER_NAME, PATIENT_HEADER_FRAGMENT_ID));
            request.setProviderNameOverride(CFLConstants.MODULE_ID);
            request.setFragmentIdOverride(PATIENT_HEADER_FRAGMENT_ID);
            return true;
        }
        return false;
    }

    private boolean isRedirectingToPersonDashboardEnabled() {
        return GlobalPropertyUtils.isTrue(CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME);
    }

    private boolean isCoreappsPatientHeader(FragmentRequest request) {
        return request.getProviderName().equals(COREAPPS_PROVIDER_NAME)
                && request.getFragmentId().equals(PATIENT_HEADER_FRAGMENT_ID);
    }
}
