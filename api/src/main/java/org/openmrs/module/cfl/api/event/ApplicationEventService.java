package org.openmrs.module.cfl.api.event;

import org.openmrs.Person;
import org.openmrs.User;

/**
 * Public API for Application level events
 */
public interface ApplicationEventService extends org.openmrs.module.emrapi.event.ApplicationEventService {

    /**
     * Fires an application level event that the specified person has been viewed by the specified
     * user.
     *
     * @should publish the person viewed event
     */
    void personViewed(Person person, User user);
}
