package org.openmrs.module.cfl.api.service;

import org.openmrs.Person;

public interface WelcomeService {

    void sendWelcomeMessages(Person person);
}
