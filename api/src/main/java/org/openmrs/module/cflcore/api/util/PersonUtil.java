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

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.module.cflcore.CFLConstants;

public final class PersonUtil {

    private static final String PHONE_NUMBER_EMPTY_CHAR = "-";

    public static String getPhoneNumber(Person person) {
        PersonAttribute phoneAttribute = person.getAttribute(CFLConstants.TELEPHONE_ATTRIBUTE_NAME);
        if (phoneAttribute == null ||
                phoneAttribute.getValue().equals(PHONE_NUMBER_EMPTY_CHAR)) {
            return "";
        } else {
            return phoneAttribute.getValue();
        }
    }

    private PersonUtil() {
    }
}
