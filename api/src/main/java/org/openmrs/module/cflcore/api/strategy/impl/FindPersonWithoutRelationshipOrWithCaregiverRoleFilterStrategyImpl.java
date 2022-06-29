/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.strategy.impl;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.copied.messages.service.ActorService;
import org.openmrs.module.cflcore.api.strategy.FindPersonFilterStrategy;

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
