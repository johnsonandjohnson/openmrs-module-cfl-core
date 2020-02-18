package org.openmrs.module.cfl.api.strategy.impl;

import org.openmrs.Person;
import org.openmrs.module.cfl.api.copied.messages.service.ActorService;
import org.openmrs.module.cfl.api.strategy.FindActorFilterStrategy;

public class FindPersonWithCaregiverRoleFilterStrategyImpl implements FindActorFilterStrategy {

    private ActorService actorService;

    @Override
    public boolean shouldBeDisplayed(Person person) {
        return actorService.isInCaregiverRole(person);
    }

    public void setActorService(ActorService actorService) {
        this.actorService = actorService;
    }
}
