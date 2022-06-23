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

import org.openmrs.Patient;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.contract.AdHocMessageSummary;
import org.openmrs.module.cflcore.api.contract.CountrySetting;
import org.openmrs.module.cflcore.api.service.AdHocMessageService;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.util.CountrySettingUtil;
import org.openmrs.module.messages.api.constants.MessagesConstants;
import org.openmrs.module.messages.api.model.PatientTemplate;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.model.ScheduledServiceGroup;
import org.openmrs.module.messages.api.service.MessagesDeliveryService;
import org.openmrs.module.messages.api.service.PatientTemplateService;
import org.openmrs.module.messages.api.service.ScheduledServiceGroupService;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * The default implementation of {@link AdHocMessageService}.
 * <p>
 * The bean is configured in resources/moduleApplicationContext.xml.
 * </p>
 */
public class AdHocMessageServiceImpl implements AdHocMessageService {

    private PatientTemplateService patientTemplateService;
    private ScheduledServiceGroupService scheduledServiceGroupService;
    private MessagesDeliveryService messagesDeliveryService;
    private ConfigService configService;

    @Override
    public AdHocMessageSummary scheduleAdHocMessage(final Date deliveryDateTime, final Set<String> channelTypes,
                                                    final Map<String, String> messageProperties,
                                                    final Collection<Patient> patients) {

        final ScheduleAdHocContext rootContext = ScheduleAdHocContext.forProperties(messageProperties);

        for (final Patient patient : patients) {
            final CountrySetting setting = CountrySettingUtil.getCountrySettingForPatient(patient);
            final Date deliveryDate = configService.getSafeMessageDeliveryDate(patient, deliveryDateTime, setting);
            final ScheduleAdHocContext patientContext = rootContext.copyWithPatientAndDate(deliveryDate, patient);

            for (final String channelType : channelTypes) {
                scheduleAdHocMessageForPatient(patientContext.copyWithChannelType(channelType));
            }
        }

        return new AdHocMessageSummary(patients.size());
    }

    public void setPatientTemplateService(PatientTemplateService patientTemplateService) {
        this.patientTemplateService = patientTemplateService;
    }

    public void setScheduledServiceGroupService(ScheduledServiceGroupService scheduledServiceGroupService) {
        this.scheduledServiceGroupService = scheduledServiceGroupService;
    }

    public void setMessagesDeliveryService(MessagesDeliveryService messagesDeliveryService) {
        this.messagesDeliveryService = messagesDeliveryService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    private void scheduleAdHocMessageForPatient(final ScheduleAdHocContext context) {
        final PatientTemplate adHocPatientTemplate = patientTemplateService.getOrCreatePatientTemplate(context.getPatient(),
                CFLConstants.AD_HOC_MESSAGE_TEMPLATE_NAME);

        final ScheduledServiceGroup adHocScheduledServiceGroup =
                scheduledServiceGroupService.createSingletonGroup(context.getDeliverDate(), context.getChannelType(),
                        adHocPatientTemplate);

        final ScheduledExecutionContext executionContext =
                new ScheduledExecutionContext(adHocScheduledServiceGroup.getScheduledServices(), context.getChannelType(),
                        context.getDeliverDate(), context.getPatient(), context.getPatient().getId(),
                        MessagesConstants.PATIENT_DEFAULT_ACTOR_TYPE, adHocScheduledServiceGroup.getId());

        executionContext.setChannelConfiguration(context.getMessageProperties());

        messagesDeliveryService.scheduleDelivery(executionContext);
    }

    /**
     * The internal helper class to wrap data needed to schedule an Ad hoc message into one object.
     */
    private static class ScheduleAdHocContext {
        private final Date deliveryDate;
        private final Patient patient;
        private final String channelType;
        private final Map<String, String> messageProperties;

        ScheduleAdHocContext(Date deliveryDate, Patient patient, String channelType, Map<String, String> messageProperties) {
            this.deliveryDate = deliveryDate;
            this.patient = patient;
            this.channelType = channelType;
            this.messageProperties = messageProperties;
        }

        static ScheduleAdHocContext forProperties(final Map<String, String> messageProperties) {
            return new ScheduleAdHocContext(null, null, null, messageProperties);
        }

        ScheduleAdHocContext copyWithChannelType(final String newChannelType) {
            return new ScheduleAdHocContext(getDeliverDate(), getPatient(), newChannelType, getMessageProperties());
        }

        ScheduleAdHocContext copyWithPatientAndDate(final Date newDeliveryDate, final Patient newPatient) {
            return new ScheduleAdHocContext(newDeliveryDate, newPatient, getChannelType(), getMessageProperties());
        }

        Date getDeliverDate() {
            return deliveryDate;
        }

        Patient getPatient() {
            return patient;
        }

        String getChannelType() {
            return channelType;
        }

        Map<String, String> getMessageProperties() {
            return messageProperties;
        }
    }
}
