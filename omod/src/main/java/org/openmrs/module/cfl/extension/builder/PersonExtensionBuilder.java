/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.extension.builder;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;

import java.util.ArrayList;
import java.util.List;

public class PersonExtensionBuilder extends ExtensionBuilder {

    public static final String FIRST_COLUMN = "firstColumnFragments";
    public static final String SECOND_COLUMN = "secondColumnFragments";
    public static final String OVERALL_ACTIONS = "overallActions";
    public static final String INCLUDE_FRAGMENTS = "includeFragments";
    public static final String VISIT = "visit";
    public static final String VISIT_ACTIONS = "visitActions";
    public static final String PATIENT_DASHBOARD = "patientDashboard";
    public static final String CLINICIAN_FACING_PATIENT_DASHBOARD = "clinicianFacingPatientDashboard";
    public static final String OTHER_ACTIONS = "otherActions";

    public PersonExtensionBuilder(AppFrameworkService appFrameworkService, String dashboard,
                                  AppContextModel contextModel) {
        super(appFrameworkService, dashboard, contextModel);
    }

    public List<Extension> buildFirstColumn() {
        return sortedExtensions(FIRST_COLUMN);
    }

    public List<Extension> buildSecondColumn() {
        return sortedExtensions(SECOND_COLUMN);
    }

    public List<Extension> buildOverallActions() {
        return sortedExtensions(OVERALL_ACTIONS);
    }

    public List<Extension> buildVisitActions() {
        return getContextModel().get(VISIT) == null ? new ArrayList<>()
                : sortedExtensions(VISIT_ACTIONS);
    }

    public List<Extension> buildOtherActions() {
        List<Extension> otherActions = getAppFrameworkService().getExtensionsForCurrentUser(
                (StringUtils.equals(getDashboard(), PATIENT_DASHBOARD) ?
                        CLINICIAN_FACING_PATIENT_DASHBOARD : getDashboard()) + "." + OTHER_ACTIONS, getContextModel());
        return sorted(otherActions);
    }

    public List<Extension> buildIncludeFragments() {
        return sortedExtensions(INCLUDE_FRAGMENTS);
    }
}
