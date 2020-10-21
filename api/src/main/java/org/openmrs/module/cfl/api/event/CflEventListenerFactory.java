package org.openmrs.module.cfl.api.event;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.api.event.listener.subscribable.BaseActionListener;

public final class CflEventListenerFactory {
    private static final Log LOGGER = LogFactory.getLog(CflEventListenerFactory.class);

    public static void registerEventListeners() {
        List<AbstractMessagesEventListener> eventComponents =
                Context.getRegisteredComponents(AbstractMessagesEventListener.class);
        for (AbstractMessagesEventListener eventListener : eventComponents) {
            subscribeListener(eventListener);
        }
        List<BaseActionListener> actionListeners =
            Context.getRegisteredComponents(BaseActionListener.class);
        for (BaseActionListener actionListener : actionListeners) {
          for (String action : actionListener.subscribeToActions()) {
            for (Class<? extends OpenmrsObject> clazz : actionListener.subscribeToObjects()) {
              Event.subscribe(clazz, action, actionListener);
            }
          }
        }
    }

    public static void unRegisterEventListeners() {
        List<AbstractMessagesEventListener> eventComponents =
                Context.getRegisteredComponents(AbstractMessagesEventListener.class);
        for (AbstractMessagesEventListener eventListener : eventComponents) {
            unSubscribeListener(eventListener);
        }
      List<BaseActionListener> actionListeners =
          Context.getRegisteredComponents(BaseActionListener.class);
      for (BaseActionListener actionListener : actionListeners) {
        for (String action : actionListener.subscribeToActions()) {
          for (Class<? extends OpenmrsObject> clazz : actionListener.subscribeToObjects()) {
            Event.unsubscribe(clazz, Event.Action.valueOf(action), actionListener);
          }
        }
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
