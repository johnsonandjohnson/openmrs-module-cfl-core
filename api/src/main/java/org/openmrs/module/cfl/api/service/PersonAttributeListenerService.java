package org.openmrs.module.cfl.api.service;

import org.openmrs.PersonAttribute;
import org.openmrs.event.Event;

/**
 * The PersonAttributeListenerService Class.
 * <p>
 * The Person Attribute Listener service is responsible for logic executed when PersonAttribute is created, updated or
 * deleted.
 * </p>
 */
public interface PersonAttributeListenerService {
    /**
     * Performs 'on attribute changed' logic.
     *
     * @param action          the type of action which caused the change of attribute (CREATED, UPDATED, VOIDED), not null
     * @param personAttribute the attribute which has change (new state), not null
     */
    void onPersonAttributeEvent(Event.Action action, PersonAttribute personAttribute);
}
