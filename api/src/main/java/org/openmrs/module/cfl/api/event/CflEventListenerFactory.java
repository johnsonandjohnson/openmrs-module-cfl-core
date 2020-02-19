package org.openmrs.module.cfl.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;

import java.util.List;

public final class CflEventListenerFactory {
    private static final Log LOGGER = LogFactory.getLog(CflEventListenerFactory.class);

    public static void registerEventListeners() {
        List<AbstractMessagesEventListener> eventComponents =
                Context.getRegisteredComponents(AbstractMessagesEventListener.class);
        for (AbstractMessagesEventListener eventListener : eventComponents) {
            subscribeListener(eventListener);
        }
    }

    public static void unRegisterEventListeners() {
        List<AbstractMessagesEventListener> eventComponents =
                Context.getRegisteredComponents(AbstractMessagesEventListener.class);
        for (AbstractMessagesEventListener eventListener : eventComponents) {
            unSubscribeListener(eventListener);
        }
    }

    private static void subscribeListener(AbstractMessagesEventListener callFlowEventListener) {
        LOGGER.debug(String.format("The CFL Module has subscribed %s listener on the %s subject.",
                callFlowEventListener.getClass().toString(), callFlowEventListener.getSubject()));
        Event.subscribe(callFlowEventListener.getSubject(), callFlowEventListener);
    }

    private static void unSubscribeListener(AbstractMessagesEventListener callFlowEventListener) {
        LOGGER.debug(String.format("The CFL Module has unsubscribed %s listener on the %s subject.",
                callFlowEventListener.getClass().toString(), callFlowEventListener.getSubject()));
        Event.unsubscribe(callFlowEventListener.getSubject(), callFlowEventListener);
    }

    private CflEventListenerFactory() {
    }
}
