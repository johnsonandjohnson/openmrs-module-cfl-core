package org.openmrs.module.cfl.api.htmlformentry.action;

import org.openmrs.module.cfl.api.exception.CflRuntimeException;
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
