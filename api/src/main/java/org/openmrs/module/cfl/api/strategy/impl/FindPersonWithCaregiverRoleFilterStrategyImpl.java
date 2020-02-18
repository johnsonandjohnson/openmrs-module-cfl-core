package org.openmrs.module.cfl.api.strategy.impl;

import org.openmrs.Person;
import org.openmrs.module.cfl.api.copied.messages.service.ActorService;
import org.openmrs.module.cfl.api.strategy.FindPersonFilterStrategy;

public class FindPersonWithCaregiverRoleFilterStrategyImpl implements FindPersonFilterStrategy {

    private ActorService actorService;

    @Override
    public boolean shouldBeReturned(Person person) {
        return actorService.isInCaregiverRole(person);
    }

    public void setActorService(ActorService actorService) {
        this.actorService = actorService;
    }
}
