package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.service.WelcomeService;
import org.openmrs.module.cfl.api.util.CountrySettingUtil;
import org.openmrs.module.cfl.handler.WelcomeMessageSender;

import java.text.MessageFormat;
import java.util.List;

public class WelcomeServiceImpl implements WelcomeService {
    private static final Log LOGGER = LogFactory.getLog(WelcomeServiceImpl.class);

    @Override
    public void sendWelcomeMessages(Person person) {
        final Patient patient = Context.getPatientService().getPatient(person.getPersonId());
        final CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(person);
        final List<WelcomeMessageSender> senders = Context.getRegisteredComponents(WelcomeMessageSender.class);

        for (final WelcomeMessageSender sender : senders) {
            try {
                sender.send(patient, countrySetting);
            } catch (Exception ex) {
                LOGGER.error(
                        MessageFormat.format("Failed to execute WelcomeMessageSender: {0}.", sender.getClass().getName()),
                        ex);
            }
        }
    }
}
