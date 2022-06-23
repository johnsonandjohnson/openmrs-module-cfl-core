/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.openmrs.module.cflcore.CFLConstants.USER_PROPERTY_NAME_LAST_VIEWED_PERSON_IDS;

public final class UserUtil {

    private static final Log LOG = LogFactory.getLog(UserUtil.class);

    /**
     * Gets the value of the user property
     * EmrApiConstants.USER_PROPERTY_NAME_LAST_VIEWED_PERSON_IDS for the user as a list of people
     * in reverse order impying the patient that was first added comes last while the last added one
     * comes first
     *
     * This method is based on org.openmrs.module.emrapi.utils.getLastViewedPatients (emrapi-api:1.26.0)
     *
     * @param passedUser
     * @should return a list of the patients last viewed by the specified user
     * @should not return voided patients
     */
    public static List<Person> getLastViewedPeople(User passedUser) {
        List<Person> lastViewed = new ArrayList<Person>();
        if (passedUser != null) {
            //The user object cached in the user's context needs to be up to date
            User user = Context.getUserService().getUser(passedUser.getId());
            String lastViewedPersonIdsString = user.getUserProperty(USER_PROPERTY_NAME_LAST_VIEWED_PERSON_IDS);
            lastViewed = getLastViewedPeople(lastViewedPersonIdsString);
        }

        Collections.reverse(lastViewed);
        return lastViewed;
    }

    private static List<Person> getLastViewedPeople(String lastViewedPersonIdsString) {
        List<Person> lastViewed = new ArrayList<Person>();
        if (StringUtils.isNotBlank(lastViewedPersonIdsString)) {
            PersonService ps = Context.getPersonService();
            String[] personIds = lastViewedPersonIdsString
                    .replaceAll("\\s", "")
                    .split(",");
            for (String pId : personIds) {
                try {
                    Person p = ps.getPerson(Integer.valueOf(pId));
                    if (p != null && !p.isVoided() && !p.isPersonVoided()) {
                        lastViewed.add(p);
                    }
                } catch (NumberFormatException e) {
                    LOG.warn(String.format("Cannot parse %s from lastViewedPersonIds list", pId));
                }
            }
        }
        return lastViewed;
    }

    private UserUtil() {
    }
}
