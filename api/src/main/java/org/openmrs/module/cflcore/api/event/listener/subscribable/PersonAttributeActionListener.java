/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.event.listener.subscribable;

import org.openmrs.OpenmrsObject;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.cflcore.api.service.PersonAttributeListenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The main listener for PersonAttribute entity. It delegates work to:
 * {@link PersonAttributeListenerService}.
 */
public class PersonAttributeActionListener extends BaseActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonAttributeActionListener.class);

    private static final List<Class<? extends OpenmrsObject>> PERSON_ATTRIBUTE_CLASS =
            Collections.singletonList(PersonAttribute.class);
    private static final List<String> ALL_ATTRIBUTE_RELEVANT_ACTIONS =
            Arrays.asList(Event.Action.CREATED.name(), Event.Action.UPDATED.name());

    private PersonAttributeListenerService personAttributeListenerService;

    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        return PERSON_ATTRIBUTE_CLASS;
    }

    @Override
    public List<String> subscribeToActions() {
        return ALL_ATTRIBUTE_RELEVANT_ACTIONS;
    }

    @Override
    public void performAction(Message message) {
        LOGGER.debug("Person Attribute Action listener triggered");
        final Event.Action action = extractAction(message);
        final PersonAttribute changedPersonAttribute = extractPersonAttribute(message);
        personAttributeListenerService.onPersonAttributeEvent(action, changedPersonAttribute);
    }

    public void setPersonAttributeListenerService(PersonAttributeListenerService personAttributeListenerService) {
        this.personAttributeListenerService = personAttributeListenerService;
    }

    private PersonAttribute extractPersonAttribute(Message message) {
        return getPersonAttribute(message);
    }

    private PersonAttribute getPersonAttribute(Message message) {
        final String personAttributeUuid = getMessagePropertyValue(message, CFLConstants.UUID_KEY);
        LOGGER.debug(String.format("Handle getPersonAttribute for '%s' uuid", personAttributeUuid));
        final PersonAttribute personAttribute = Context.getPersonService().getPersonAttributeByUuid(personAttributeUuid);
        if (personAttribute == null) {
            throw new CflRuntimeException("Unable to retrieve person attribute by uuid: " + personAttributeUuid);
        }
        return personAttribute;
    }
}
