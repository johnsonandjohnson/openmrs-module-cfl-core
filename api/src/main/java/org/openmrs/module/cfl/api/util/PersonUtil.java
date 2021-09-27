package org.openmrs.module.cfl.api.util;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.module.cfl.CFLConstants;

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
