package org.openmrs.module.cfl.api.copied.messages.service;

import org.openmrs.Person;
import org.openmrs.module.cfl.api.copied.messages.model.ActorType;

import java.util.List;

public interface ActorService {

    boolean isInCaregiverRole(Person patient);

    List<ActorType> getAllActorTypes();
}
