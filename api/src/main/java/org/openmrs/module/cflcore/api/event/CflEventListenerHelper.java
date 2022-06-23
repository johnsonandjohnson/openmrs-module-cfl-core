/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.event.listener.subscribable.BaseListener;

import java.util.List;

public final class CflEventListenerHelper {
    private static final Log LOGGER = LogFactory.getLog(CflEventListenerHelper.class);

    public static void registerEventListeners() {
        final List<BaseListener> listeners = Context.getRegisteredComponents(BaseListener.class);
        for (BaseListener listener : listeners) {
            LOGGER.info(String.format("The CFL Module has subscribed %s listener to: %s.", listener.getClass().toString(),
                    listener.getSubscriptionDescription()));
            listener.subscribeSelf();
        }
    }

    public static void unRegisterEventListeners() {
        final List<BaseListener> listeners = Context.getRegisteredComponents(BaseListener.class);
        for (BaseListener listener : listeners) {
            LOGGER.info(
                    String.format("The CFL Module has unsubscribed %s listener from: %s.", listener.getClass().toString(),
                            listener.getSubscriptionDescription()));
            listener.unsubscribeSelf();
        }
    }

    private CflEventListenerHelper() {
    }
}
