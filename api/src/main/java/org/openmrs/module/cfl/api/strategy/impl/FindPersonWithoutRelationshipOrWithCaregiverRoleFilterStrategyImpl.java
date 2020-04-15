package org.openmrs.module.cfl.api.strategy.impl;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.copied.messages.service.ActorService;
import org.openmrs.module.cfl.api.strategy.FindPersonFilterStrategy;

public class FindPersonWithoutRelationshipOrWithCaregiverRoleFilterStrategyImpl implements FindPersonFilterStrategy {

    private ActorService actorService;

    @Override
    public boolean shouldBeReturned(Person person) {
        int relationshipCount = Context.getPersonService().getRelationshipsByPerson(person).size();
        return relationshipCount == 0 || actorService.isInCaregiverRole(person);
    }

    public void setActorService(ActorService actorService) {
        this.actorService = actorService;
    }
}
