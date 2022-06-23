/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.contract.CountrySetting;
import org.openmrs.module.cflcore.api.service.WelcomeService;
import org.openmrs.module.cflcore.api.util.CountrySettingUtil;
import org.openmrs.module.cflcore.handler.WelcomeMessageSender;

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
