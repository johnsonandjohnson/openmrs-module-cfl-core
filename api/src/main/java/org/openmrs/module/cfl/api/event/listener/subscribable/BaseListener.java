package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.event.EventListener;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;

/**
 * The BaseListener Class, it's the base class for all Event listener beans in the CfL module.
 * <p>
 * All beans which derive from the {@code BaseListener} are automatically registered in {@link org.openmrs.event.Event}
 * system at the CfL module's startup.
 * </p>
 *
 * @see BaseActionListener BaseActionListener for listners on entity CRUD events
 * @see AbstractMessagesEventListener AbstractMessagesEventListener for listners of arbitrary events
 */
public abstract class BaseListener implements EventListener, DaemonTokenAware {

    private DaemonToken daemonToken;

    @Override
    public void setDaemonToken(DaemonToken daemonToken) {
        this.daemonToken = daemonToken;
    }

    /**
     * Subscribe this instance in the OpenMRS Event system.
     */
    public abstract void subscribeSelf();

    /**
     * Unsubscribe this instance from OpenMRS Event system.
     */
    public abstract void unsubscribeSelf();

    /**
     * Gets description of subscription kind of this instance.
     * <p>
     * The result of this method is used in log outputs.
     * </p>
     *
     * @return the subscription description
     */
    public abstract String getSubscriptionDescription();

    /**
     * @return daemon token set for this instance or null of the token was not set
     */
    protected DaemonToken getDaemonToken() {
        return daemonToken;
    }
}
