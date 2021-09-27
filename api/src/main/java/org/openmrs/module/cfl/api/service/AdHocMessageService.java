package org.openmrs.module.cfl.api.service;

import org.openmrs.Patient;
import org.openmrs.module.cfl.api.contract.AdHocMessageSummary;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface AdHocMessageService {

    AdHocMessageSummary scheduleAdHocMessage(final Date deliveryDateTime, final Set<String> channelTypes,
                                             final Map<String, String> messageProperties,
                                             final Collection<Patient> patients);
}
