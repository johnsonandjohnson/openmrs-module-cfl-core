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
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
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

    protected String getVisitUuid(Message message) {
        return getMessagePropertyValue(message, CFLConstants.UUID_KEY);
    }

    protected Visit getVisit(String visitUuid) {
        LOGGER.debug("Handle getVisit for {} uuid", visitUuid);
        Visit visit = Context.getVisitService().getVisitByUuid(visitUuid);
        if (visit == null) {
            throw new CflRuntimeException(String.format("Unable to retrieve visit by uuid: %s", visitUuid));
        }
        return visit;
    }
}
