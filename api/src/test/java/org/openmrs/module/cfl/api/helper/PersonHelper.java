package org.openmrs.module.cfl.api.helper;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.cfl.api.contract.CFLPerson;

import java.util.List;

public final class PersonHelper {

    public static final String JOHN_GIVEN_NAME = "John";

    public static final String JOHN_FAMILY_NAME = "Doe";

    public static final String ADAM_GIVEN_NAME = "Adam";

    public static final String ADAM_FAMILY_NAME = "Smith";

    public static boolean containsPerson(List<CFLPerson> cflPeople, String fullName) {
        boolean contain = false;
        for (CFLPerson cflPerson : cflPeople) {
            if (StringUtils.equals(fullName, cflPerson.getPerson().getPersonName().toString())) {
                contain = true;
                break;
            }
        }

        return contain;
    }

    private PersonHelper() {
    }
}
