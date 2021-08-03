package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class VisitEncounterListener extends EncounterActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisitEncounterListener.class);

    @Override
    public List<String> subscribeToActions() {
        return Arrays.asList(Event.Action.CREATED.name(), Event.Action.UPDATED.name(), Event.Action.VOIDED.name());
    }

    @Override
    public void performAction(Message message)  {
        LOGGER.debug("Visit Encounter listener triggered");
        Encounter encounter = extractEncounter(message);
        Visit encounterVisit = encounter.getVisit();
        if (encounterVisit != null) {
            encounterVisit.setDateChanged(new Date());
            encounterVisit.setChangedBy(Context.getAuthenticatedUser());
            Context.getVisitService().saveVisit(encounterVisit);
        }
    }
}
