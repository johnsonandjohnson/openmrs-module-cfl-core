package org.openmrs.module.cfl.api.strategy;

import org.openmrs.Person;

public interface FindActorFilterStrategy {

    boolean shouldBeDisplayed(Person person);
}
