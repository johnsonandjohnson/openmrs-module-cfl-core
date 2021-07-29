package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.Event;
import org.openmrs.event.SubscribableEventListener;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.List;

public abstract class BaseActionListener implements SubscribableEventListener, DaemonTokenAware {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseActionListener.class);

  private DaemonToken daemonToken;

  private Event event = new Event();

  /**
   * Defines the list of Actions which will be performed {@link #performAction(Message)} by this listener
   * @return a list of supported Actions by this listener
   */
  public abstract List<String> subscribeToActions();

  /**
   * Performs action on specific event.
   * @param message - processed message with provided properties
   */
  public abstract void performAction(Message message);

  /**
   * Defines the list of classes which will be handled {@link #performAction(Message)} by this listener
   * @return a list of classes that this can handle
   */
  public abstract List<Class<? extends OpenmrsObject>> subscribeToObjects();

  /**
   * Handles messages received by this listener
   * @param message - processed message with provided properties
   */
  @Override
  public void onMessage(final Message message) {
    LOGGER.trace("Handle onMessage");
    Daemon.runInDaemonThread(new Runnable() {
      @Override
      public void run() {
        performAction(message);
      }
    }, daemonToken);
  }

  public void init() {
    LOGGER.debug("The PeopleActionListener was initialized and registered as a subscriber");
    event.setSubscription(this);
  }

  public void close() {
    LOGGER.debug("The PeopleActionListener was unsubscribe");
    event.unsetSubscription(this);
  }

  public void setDaemonToken(DaemonToken daemonToken) {
    this.daemonToken = daemonToken;
  }

  /**
   * Extracts an Action which caused the {@code message}.
   *
   * @param message the Event message, not null
   * @return the Action which cause the Event, never null
   * @throws IllegalArgumentException if {@code message} doesn't contain action or the value is wrong
   */
  protected Event.Action extractAction(Message message) {
    final String actionName = getMessagePropertyValue(message, CFLConstants.ACTION_KEY);
    try {
      return Event.Action.valueOf(actionName);
    } catch (IllegalArgumentException eae) {
      throw new CflRuntimeException("Unable to retrieve event action for name: " + actionName, eae);
    }
  }

  protected String getMessagePropertyValue(Message message, String propertyName) {
    LOGGER.debug(String.format("Handle getMessagePropertyValue for '%s' property", propertyName));
    try {
      validateMessage(message);
      return ((MapMessage) message).getString(propertyName);
    } catch (JMSException e) {
      throw new CflRuntimeException("Exception while get uuid of created object from JMS message. " + e, e);
    }
  }

  private void validateMessage(Message message) {
    if (!(message instanceof MapMessage)) {
      throw new CflRuntimeException("Event message has to be MapMessage");
    }
  }
}
