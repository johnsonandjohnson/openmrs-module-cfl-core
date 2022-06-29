/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.action;

import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

/**
 * The CustomFormSubmissionActionBeanBridge Class.
 * <p>
 * The base class for CustomFormSubmissionAction bridges to optional CustomFormSubmissionAction, which may not be in
 * classpath.
 * </p>
 */
public class CustomFormSubmissionActionBeanBridge implements CustomFormSubmissionAction {
    private final String formSubmissionActionClassName;

    CustomFormSubmissionActionBeanBridge(final String formSubmissionActionClass) {
        this.formSubmissionActionClassName = formSubmissionActionClass;
    }

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        try {
            final CustomFormSubmissionAction action =
                    (CustomFormSubmissionAction) Class.forName(formSubmissionActionClassName).newInstance();
            action.applyAction(formEntrySession);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw new CflRuntimeException(
                    "Failed to run CustomFormSubmissionActionBeanBridge for class: " + formSubmissionActionClassName, e);
        }
    }
}
