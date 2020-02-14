package org.openmrs.module.cfl.api.strategy.impl;

import org.openmrs.Person;
import org.openmrs.module.cfl.api.strategy.FindActorFilterStrategy;

public class FindPersonWithCflRelationshipFilterStrategyImpl implements FindActorFilterStrategy {

    @Override
    public boolean shouldBeDisplayed(Person person) {
        return true;
    }
}
