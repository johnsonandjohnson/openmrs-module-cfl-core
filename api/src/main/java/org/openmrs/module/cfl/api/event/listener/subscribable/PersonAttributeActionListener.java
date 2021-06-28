package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.OpenmrsObject;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

public abstract class PersonAttributeActionListener extends BaseActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonAttributeActionListener.class);

    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        final List<Class<? extends OpenmrsObject>> list = new ArrayList<Class<? extends OpenmrsObject>>();
        list.add(PersonAttribute.class);
        return list;
    }

    protected PersonAttribute extractPersonAttribute(Message message) {
        LOGGER.debug("Person Attribute Action listener triggered");
        return getPersonAttribute(message);
    }

    private PersonAttribute getPersonAttribute(Message message) {
        final String personAttributeUuid = getMessagePropertyValue(message, CFLConstants.UUID_KEY);
        LOGGER.debug(String.format("Handle getPersonAttribute for '%s' uuid", personAttributeUuid));
        final PersonAttribute personAttribute = Context.getPersonService()
                                                       .getPersonAttributeByUuid(personAttributeUuid);
        if (personAttribute == null) {
            throw new CflRuntimeException(
                    String.format("Unable to retrieve person attribute by uuid: %s", personAttributeUuid));
        }
        return personAttribute;
    }
}
