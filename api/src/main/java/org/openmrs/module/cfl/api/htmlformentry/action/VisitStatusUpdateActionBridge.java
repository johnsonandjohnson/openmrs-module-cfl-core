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
