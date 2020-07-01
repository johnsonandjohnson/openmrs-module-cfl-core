package org.openmrs.module.cfl.page.requestmapper;

import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.page.PageRequestMapper;
import org.springframework.stereotype.Component;

/**
 * Overrides the mapping to the login page for the reference application to a customized page for Connect for Life
 */
@Component
public class CflLoginPageRequestMapper implements PageRequestMapper {

    private static final String REFAPP_PROVIDER_NAME = "referenceapplication";

    private static final String REFAPP_LOGIN_PAGE_NAME = "login";

    private static final String CFL_LOGIN_PAGE_NAME = "cflLogin";

    @Override
    public boolean mapRequest(PageRequest request) {
        if (request.getProviderName().equals(REFAPP_PROVIDER_NAME) &&
                (request.getPageName().equals(REFAPP_LOGIN_PAGE_NAME))) {
                    request.setProviderName(CFLConstants.MODULE_ID);
                    request.setPageNameOverride(CFL_LOGIN_PAGE_NAME);
                    return true;
        }
        return false;
    }
}
