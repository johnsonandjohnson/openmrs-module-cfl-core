package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.IrisVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.jms.Message;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UpdatingVisitListener extends VisitActionListener {

    @Autowired
    private IrisVisitService irisVisitService;

    @Override
    public List<String> subscribeToActions() {
        return Collections.singletonList(Event.Action.UPDATED.name());
    }

    @Override
    public void performAction(Message message) {
        if (getConfigService().isVaccinationInfoIsEnabled()) {
            Visit updatedVisit = extractVisit(message);

            String visitStatus = "";
            Collection<VisitAttribute> activeAttributes = updatedVisit.getActiveAttributes();
            for (VisitAttribute visitAttribute : activeAttributes) {
                if (visitAttribute.getAttributeType().getUuid().equals(CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID)) {
                    visitStatus = visitAttribute.getValueReference();
                }
            }

            if (visitStatus.equals(Context.getAdministrationService()
                                          .getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY))) {
                irisVisitService.createFutureVisits(updatedVisit);
            }
        }
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }
}
