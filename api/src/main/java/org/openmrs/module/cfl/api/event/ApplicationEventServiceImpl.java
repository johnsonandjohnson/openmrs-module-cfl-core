package org.openmrs.module.cfl.api.event;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.event.Event;
import org.openmrs.event.EventMessage;
import org.openmrs.module.cfl.CFLConstants;

public class ApplicationEventServiceImpl extends BaseOpenmrsService implements ApplicationEventService {

    private org.openmrs.module.emrapi.event.ApplicationEventService emrapiAppEventService;

    @Override
    public void personViewed(Person person, User user) {
        EventMessage eventMessage = new EventMessage();
        eventMessage.put(CFLConstants.EVENT_KEY_PERSON_UUID, person.getUuid());
        eventMessage.put(CFLConstants.EVENT_KEY_USER_UUID, user.getUuid());
        Event.fireEvent(CFLConstants.EVENT_TOPIC_NAME_PERSON_VIEWED, eventMessage);
    }

    @Override
    public void patientViewed(Patient patient, User user) {
        emrapiAppEventService.patientViewed(patient, user);
    }

    public void setEmrapiAppEventService(
            org.openmrs.module.emrapi.event.ApplicationEventService emrapiAppEventService) {
        this.emrapiAppEventService = emrapiAppEventService;
    }
}
