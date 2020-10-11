package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.OpenmrsObject;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

public abstract class VisitActionListener extends BaseActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisitActionListener.class);

    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        List<Class<? extends OpenmrsObject>> list = new ArrayList<Class<? extends OpenmrsObject>>();
        list.add(Visit.class);
        return list;
    }

    protected Visit extractVisit(Message message) {
        LOGGER.trace("Handle extractVisit");
        String visitUuid = getMessagePropertyValue(message, CFLConstants.UUID_KEY);
        return getVisit(visitUuid);
    }

    private Visit getVisit(String visitUuid) {
        LOGGER.debug(String.format("Handle getVisit for %s uuid", visitUuid));
        Visit visit = Context.getVisitService().getVisitByUuid(visitUuid);
        if (visit == null) {
            throw new CflRuntimeException(String.format("Unable to retrieve visit by uuid: %s", visitUuid));
        }
        return visit;
    }
}
