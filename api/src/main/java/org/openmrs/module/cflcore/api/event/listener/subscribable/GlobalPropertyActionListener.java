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

import org.openmrs.GlobalProperty;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;

import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

public abstract class GlobalPropertyActionListener extends BaseActionListener {

    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        List<Class<? extends OpenmrsObject>> list = new ArrayList<Class<? extends OpenmrsObject>>();
        list.add(GlobalProperty.class);
        return list;
    }

    protected GlobalProperty extractGlobalProperty(Message message) {
        String globalPropertyUuid = getMessagePropertyValue(message, CFLConstants.UUID_KEY);
        return getGlobalProperty(globalPropertyUuid);
    }

    private GlobalProperty getGlobalProperty(String globalPropertyUuid) {
        GlobalProperty globalProperty = Context.getAdministrationService().getGlobalPropertyByUuid(globalPropertyUuid);
        if (globalProperty == null) {
            throw new CflRuntimeException(String.format("Unable to retrieve global property by uuid: %s",
                    globalPropertyUuid));
        }
        return globalProperty;
    }
}
