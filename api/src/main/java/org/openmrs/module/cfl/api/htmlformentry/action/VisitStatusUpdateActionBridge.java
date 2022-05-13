/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.htmlformentry.action;

import org.openmrs.annotation.OpenmrsProfile;

/**
 * The VisitStatusUpdateActionBridge Class.
 * <p>
 * Bridge to VisitStatusUpdateAction. This bean is loaded only if visits module is loaded.
 * </p>
 */
@OpenmrsProfile(modules = {"visits:1.*"})
public class VisitStatusUpdateActionBridge extends CustomFormSubmissionActionBeanBridge {
    public VisitStatusUpdateActionBridge() {
        // The pass string name might not be needed, because this bean should not be loaded without visits in classpath
        super("org.openmrs.module.visits.api.htmlformentry.action.VisitStatusUpdateAction");
    }
}
