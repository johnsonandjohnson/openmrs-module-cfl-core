package org.openmrs.module.cfl.handler.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.PersonService;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.cfl.api.util.PersonUtil;
import org.openmrs.module.cfl.handler.WelcomeMessageSender;
import org.openmrs.module.messages.api.builder.PatientTemplateBuilder;
import org.openmrs.module.messages.api.constants.MessagesConstants;
import org.openmrs.module.messages.api.model.PatientTemplate;
import org.openmrs.module.messages.api.model.ScheduledExecutionContext;
import org.openmrs.module.messages.api.model.ScheduledService;
import org.openmrs.module.messages.api.model.ScheduledServiceGroup;
import org.openmrs.module.messages.api.model.ScheduledServiceParameter;
import org.openmrs.module.messages.api.model.Template;
import org.openmrs.module.messages.api.model.types.ServiceStatus;
import org.openmrs.module.messages.api.service.MessagesDeliveryService;
import org.openmrs.module.messages.api.service.MessagingGroupService;
import org.openmrs.module.messages.api.service.PatientTemplateService;
import org.openmrs.module.messages.api.service.TemplateService;
import org.openmrs.module.messages.domain.criteria.PatientTemplateCriteria;
import org.openmrs.module.messages.domain.criteria.TemplateCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;

/**
 * The base class for {@link WelcomeMessageSender}s.
 */
public abstract class BaseWelcomeMessageSenderImpl implements WelcomeMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseWelcomeMessageSenderImpl.class);

    private final String channelType;

    private TemplateService templateService;
    private PatientTemplateService patientTemplateService;
    private MessagesDeliveryService messagesDeliveryService;
    private MessagingGroupService messagingGroupService;
    private PersonService personService;
    private ConfigService configService;

    public BaseWelcomeMessageSenderImpl(final String channelType) {
        this.channelType = channelType;
    }

    protected ScheduledExecutionContext decorateScheduledExecutionContext(
            final ScheduledExecutionContext scheduledExecutionContext, CountrySetting setting) {
        return scheduledExecutionContext;
    }

    protected abstract boolean isSendOnPatientRegistrationEnabled(CountrySetting settings);

    @Override
    public void send(Patient patient, CountrySetting settings) {
        if (!isSendOnPatientRegistrationEnabled(settings)) {
            return;
        }

        final Date welcomeMessageDeliveryTime = configService.getSafeMessageDeliveryDate(patient, DateUtil.now(), settings);
        final PatientTemplate welcomeMessagePatientTemplate = getOrCreateWelcomeMessagePatientTemplate(patient);

        final ScheduledServiceGroup scheduledServiceGroup =
                createScheduledServiceGroup(welcomeMessageDeliveryTime, welcomeMessagePatientTemplate);

        final ScheduledExecutionContext executionContext =
                new ScheduledExecutionContext(scheduledServiceGroup.getScheduledServices(), channelType,
                        welcomeMessageDeliveryTime, patient, patient.getId(), MessagesConstants.PATIENT_DEFAULT_ACTOR_TYPE,
                        scheduledServiceGroup.getId());

        final String patientPhoneNumber = PersonUtil.getPhoneNumber(patient);
        if (StringUtils.isNotBlank(patientPhoneNumber)) {
            messagesDeliveryService.scheduleDelivery(decorateScheduledExecutionContext(executionContext, settings));
        } else {
            LOGGER.error(String.format("Patient %s with id %d does not have an assigned phone number. " +
                    "Welcome message will not be sent.", patient.getGivenName() + " " + patient.getFamilyName(),
                    patient.getId()));
        }
    }

    private PatientTemplate getOrCreateWelcomeMessagePatientTemplate(final Patient patient) {
        final Template welcomeMessageTemplate = getWelcomeMessageTemplate();

        final PatientTemplate existingPatientWelcomeMessageTemplate = patientTemplateService.findOneByCriteria(
                PatientTemplateCriteria.forPatientAndActorAndTemplate(patient.getId(), patient.getId(),
                        welcomeMessageTemplate.getId()));

        final PatientTemplate welcomeMessagePatientTemplate;

        if (existingPatientWelcomeMessageTemplate == null) {
            final PatientTemplate newPatientTemplate = new PatientTemplateBuilder(welcomeMessageTemplate, patient).build();
            welcomeMessagePatientTemplate = patientTemplateService.saveOrUpdate(newPatientTemplate);
        } else {
            welcomeMessagePatientTemplate = existingPatientWelcomeMessageTemplate;
        }

        return welcomeMessagePatientTemplate;
    }

    private Template getWelcomeMessageTemplate() {
        final Template welcomeMessageTemplate =
                templateService.findOneByCriteria(TemplateCriteria.forName(CFLConstants.WELCOME_MESSAGE_TEMPLATE));

        if (welcomeMessageTemplate == null) {
            throw new IllegalStateException("Failed to send Welcome Message, missing Messages Template with name: " +
                    CFLConstants.WELCOME_MESSAGE_TEMPLATE);
        }

        return welcomeMessageTemplate;
    }

    private ScheduledServiceGroup createScheduledServiceGroup(final Date welcomeMessageDeliveryTime,
                                                              final PatientTemplate welcomeMessagePatientTemplate) {
        final ScheduledService scheduledService = new ScheduledService();
        scheduledService.setStatus(ServiceStatus.PENDING);
        scheduledService.setPatientTemplate(welcomeMessagePatientTemplate);
        scheduledService.setScheduledServiceParameters(Collections.<ScheduledServiceParameter>emptyList());
        scheduledService.setService(welcomeMessagePatientTemplate.getTemplate().getName());

        final ScheduledServiceGroup scheduledServiceGroup = new ScheduledServiceGroup();
        scheduledServiceGroup.setPatient(welcomeMessagePatientTemplate.getPatient());
        scheduledServiceGroup.setActor(welcomeMessagePatientTemplate.getPatient());
        scheduledServiceGroup.setStatus(ServiceStatus.PENDING);

        scheduledServiceGroup.setChannelType(channelType);
        scheduledServiceGroup.setMsgSendTime(welcomeMessageDeliveryTime);

        scheduledServiceGroup.getScheduledServices().add(scheduledService);
        scheduledService.setGroup(scheduledServiceGroup);

        return messagingGroupService.saveGroup(scheduledServiceGroup);
    }

    public TemplateService getTemplateService() {
        return templateService;
    }

    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    public PatientTemplateService getPatientTemplateService() {
        return patientTemplateService;
    }

    public void setPatientTemplateService(PatientTemplateService patientTemplateService) {
        this.patientTemplateService = patientTemplateService;
    }

    public MessagesDeliveryService getMessagesDeliveryService() {
        return messagesDeliveryService;
    }

    public void setMessagesDeliveryService(MessagesDeliveryService messagesDeliveryService) {
        this.messagesDeliveryService = messagesDeliveryService;
    }

    public MessagingGroupService getMessagingGroupService() {
        return messagingGroupService;
    }

    public void setMessagingGroupService(MessagingGroupService messagingGroupService) {
        this.messagingGroupService = messagingGroupService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
