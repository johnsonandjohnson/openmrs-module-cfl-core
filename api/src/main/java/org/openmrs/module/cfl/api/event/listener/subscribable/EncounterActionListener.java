package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.Encounter;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;

import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

public abstract class EncounterActionListener extends BaseActionListener {
    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        final List<Class<? extends OpenmrsObject>> list = new ArrayList<>();
        list.add(Encounter.class);
        return list;
    }

    protected Encounter extractEncounter(Message message) {
        final String encounterUuid = getMessagePropertyValue(message, CFLConstants.UUID_KEY);
        return getEncounter(encounterUuid);
    }

    private Encounter getEncounter(String encounterUuid) {
        final Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
        if (encounter == null) {
            throw new CflRuntimeException(String.format("Unable to retrieve encounter by uuid: %s", encounterUuid));
        }
        return encounter;
    }
}
