package org.openmrs.module.cfl.api.event.listener.subscribable;

import java.util.ArrayList;
import java.util.List;
import javax.jms.Message;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.PersonService;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for subscribable event listening.
 */
public abstract class PeopleActionListener extends BaseActionListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(PeopleActionListener.class);

  private PersonService personService;

  /**
   * @see BaseActionListener#subscribeToObjects()
   */
  @Override
  public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
    List<Class<? extends OpenmrsObject>> list = new ArrayList<Class<? extends OpenmrsObject>>();
    list.add(Patient.class);
    return list;
  }

  public void setPersonService(PersonService personService) {
    this.personService = personService;
  }

  public PersonService getPersonService() {
    return personService;
  }

  /**
   * Extracts the person from the received messages.
   * Fetch the {@link Person} object from DB based on UUID from message properties.
   * @param message message with properties.
   * @return retrieved patient
   */
  protected Person extractPerson(Message message) {
    LOGGER.trace("Handle extractPerson");
    String personUuid = getMessagePropertyValue(message, CFLConstants.UUID_KEY);
    return getPerson(personUuid);
  }

  private Person getPerson(String personUuid) {
    LOGGER.debug(String.format("Handle getPerson for '%s' uuid", personUuid));
    Person person = personService.getPersonByUuid(personUuid);
    if (person == null) {
      throw new CflRuntimeException(String.format("Unable to retrieve person by uuid: %s", personUuid));
    }
    return person;
  }
}
