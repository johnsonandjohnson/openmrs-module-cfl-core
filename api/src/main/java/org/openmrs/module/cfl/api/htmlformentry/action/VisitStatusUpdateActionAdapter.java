package org.openmrs.module.cfl.api.htmlformentry.action;

import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

/**
 * The VisitStatusUpdateActionAdapter Class.
 * <p>
 * This implementation delegates work to {@link VisitStatusUpdateActionBridge} bean which only exists if the visits
 * module is loaded. The VisitStatusUpdateActionBridge delegates to the actual Update Action implemented in visits
 * module. If there is no visits module, then this Form Submission Action does nothing.
 * </p>
 */
public class VisitStatusUpdateActionAdapter implements CustomFormSubmissionAction {
    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        Context
                .getRegisteredComponents(VisitStatusUpdateActionBridge.class)
                .stream()
                .findFirst()
                .ifPresent(c -> c.applyAction(formEntrySession));
    }
}
