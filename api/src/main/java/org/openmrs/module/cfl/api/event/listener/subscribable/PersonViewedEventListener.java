package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.util.Properties;
import org.openmrs.module.cfl.api.util.UserUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Listens for patient viewed events, the patient found in the message payload gets added to the
 * last viewed patients user property of the specified user,
 */
public class PersonViewedEventListener extends AbstractMessagesEventListener {

    private static final int MAX_CHARACTERS_IN_PROPERTY = 255;
    private static final int ONE = 1;

    @Override
    public String getSubject() {
        return CFLConstants.EVENT_TOPIC_NAME_PERSON_VIEWED;
    }

    /**
     * Processes the specified jms message
     * <p>
     * The method is based on org.openmrs.module.emrapi.event.PatientViewedEventListener.processMessage
     * (emrapi-api:1.26.0)
     *
     * @should add the person to the last viewed user property
     * @should remove the first person and add the new one to the start if the list is full
     * @should not add a duplicate and should move the existing person to the start
     * @should not remove any person if a duplicate is added to a full list
     */
    @Override
    protected void handleEvent(Properties properties) {
        String personUuid = properties.getString(CFLConstants.EVENT_KEY_PERSON_UUID);
        String userUuid = properties.getString(CFLConstants.EVENT_KEY_USER_UUID);
        Person personToAdd = Context.getPersonService().getPersonByUuid(personUuid);
        if (personToAdd == null || personToAdd.getId() == null) {
            throw new APIException(
                    String.format("Failed to find a person with uuid: %s or the person is not yet saved", personUuid));
        }

        UserService userService = Context.getUserService();
        User user = userService.getUserByUuid(userUuid);
        if (user != null) {
            List<Integer> personIds = getNewListWithUniqueIds(personToAdd, user);

            String property = StringUtils.join(personIds, ",");
            if (StringUtils.isNotBlank(property) && property.length() > MAX_CHARACTERS_IN_PROPERTY) {
                //exceeded the user property max size and hence needs trimming.
                //find the last comma before index 255 and cut off from there
                //RA-200 Wyclif says patients ids at the end of the string are the most recent
                //so that is why we trim from beginning instead of end.
                property = property.substring(property.indexOf(',', property.length() - MAX_CHARACTERS_IN_PROPERTY) + ONE);
            }

            userService.setUserProperty(user, CFLConstants.USER_PROPERTY_NAME_LAST_VIEWED_PERSON_IDS, property);
        }
    }

    private List<Integer> getNewListWithUniqueIds(Person personToAdd, User user) {
        int limit = getConfigService().getLastViewedPersonSizeLimit();
        List<Integer> personIds = new ArrayList<Integer>(limit);
        if (limit > 0) {
            List<Person> lastViewedPerson = UserUtil.getLastViewedPeople(user);
            personIds.add(personToAdd.getId());
            for (Person p : lastViewedPerson) {
                if (personIds.size() == limit) {
                    break;
                }
                if (personIds.contains(p.getId())) {
                    continue;
                }

                personIds.add(p.getId());
            }

            Collections.reverse(personIds);
        }
        return personIds;
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent("cfl.configService", ConfigService.class);
    }
}
