package org.openmrs.module.cfl.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.event.listener.subscribable.BaseListener;

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
