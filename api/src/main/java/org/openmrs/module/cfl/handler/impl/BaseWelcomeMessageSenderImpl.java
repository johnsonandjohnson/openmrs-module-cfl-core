package org.openmrs.module.cfl.handler.impl;

import org.openmrs.Patient;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.util.DateUtil;
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
import org.openmrs.module.messages.api.util.BestContactTimeHelper;
import org.openmrs.module.messages.domain.criteria.PatientTemplateCriteria;
import org.openmrs.module.messages.domain.criteria.TemplateCriteria;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * The base class for {@link WelcomeMessageSender}s.
 */
public abstract class BaseWelcomeMessageSenderImpl implements WelcomeMessageSender {
    private final String channelType;

    private TemplateService templateService;
    private PatientTemplateService patientTemplateService;
    private MessagesDeliveryService messagesDeliveryService;
    private MessagingGroupService messagingGroupService;
    private PersonService personService;

    public BaseWelcomeMessageSenderImpl(final String channelType) {
        this.channelType = channelType;
    }

    protected ScheduledExecutionContext decorateScheduledExecutionContext(
            final ScheduledExecutionContext scheduledExecutionContext) {
        return scheduledExecutionContext;
    }

    protected abstract boolean isSendOnPatientRegistrationEnabled(CountrySetting settings);

    @Override
    public void send(Patient patient, CountrySetting settings) {
        if (!isSendOnPatientRegistrationEnabled(settings)) {
            return;
        }

        final PatientTemplate welcomeMessagePatientTemplate = getWelcomeMessagePatientTemplate(patient);
        final Date welcomeMessageDeliveryTime = getWelcomeMessageDeliveryDate(patient, settings);

        final ScheduledService scheduledService = new ScheduledService();
        scheduledService.setStatus(ServiceStatus.PENDING);
        scheduledService.setPatientTemplate(welcomeMessagePatientTemplate);
        scheduledService.setScheduledServiceParameters(Collections.<ScheduledServiceParameter>emptyList());
        scheduledService.setService(welcomeMessagePatientTemplate.getTemplate().getName());

        final ScheduledServiceGroup scheduledServiceGroup = new ScheduledServiceGroup();
        scheduledServiceGroup.setPatient(welcomeMessagePatientTemplate.getPatient());
        scheduledServiceGroup.setActor(patient);
        scheduledServiceGroup.setStatus(ServiceStatus.PENDING);

        scheduledServiceGroup.setChannelType(channelType);
        scheduledServiceGroup.setMsgSendTime(welcomeMessageDeliveryTime);

        scheduledServiceGroup.getScheduledServices().add(scheduledService);
        scheduledService.setGroup(scheduledServiceGroup);

        messagingGroupService.saveGroup(scheduledServiceGroup);

        final ScheduledExecutionContext executionContext =
                new ScheduledExecutionContext(scheduledServiceGroup.getScheduledServices(), channelType,
                        welcomeMessageDeliveryTime, patient, patient.getId(), MessagesConstants.PATIENT_DEFAULT_ACTOR_TYPE,
                        scheduledServiceGroup.getId());
        messagesDeliveryService.scheduleDelivery(decorateScheduledExecutionContext(executionContext));
    }

    private Date getWelcomeMessageDeliveryDate(final Patient patient, final CountrySetting settings) {
        final TimeZone defaultUserTimezone = getDefaultUserTimezone();
        final Date now = DateUtil.now();
        final Date allowedTimeWindowFrom =
                DateUtil.getDateWithTimeOfDay(now, settings.getAllowOnPatientRegistrationTimeFrom(), defaultUserTimezone);
        final Date allowedTimeWindowTo =
                DateUtil.getDateWithTimeOfDay(now, settings.getAllowOnPatientRegistrationTimeTo(), defaultUserTimezone);

        if (now.before(allowedTimeWindowFrom) || now.after(allowedTimeWindowTo)) {
            return getNextDayBestContactTime(patient, defaultUserTimezone);
        }

        return now;
    }

    private TimeZone getDefaultUserTimezone() {
        final String defaultTimezoneName = Context
                .getAdministrationService()
                .getGlobalProperty(org.openmrs.module.messages.api.constants.ConfigConstants.DEFAULT_USER_TIMEZONE);
        return TimeZone.getTimeZone(defaultTimezoneName);
    }

    private Date getNextDayBestContactTime(final Patient patient, final TimeZone timeZone) {
        final RelationshipType caregiverType =
                personService.getRelationshipTypeByUuid(CFLConstants.CAREGIVER_RELATIONSHIP_UUID);
        final String bestContactTime = BestContactTimeHelper.getBestContactTime(patient, caregiverType);

        return DateUtil.getDateWithTimeOfDay(getTomorrow(timeZone), bestContactTime, timeZone);
    }

    private Date getTomorrow(final TimeZone timeZone) {
        final Calendar calendar = Calendar.getInstance(timeZone);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private PatientTemplate getWelcomeMessagePatientTemplate(final Patient patient) {
        final Template welcomeMessageTemplate = getWelcomeMessageTemplate();

        final PatientTemplate existingPatientWelcomeMessageTemplate = patientTemplateService.findOneByCriteria(
                PatientTemplateCriteria.forPatientAndActorAndTemplate(patient.getId(), patient.getId(),
                        welcomeMessageTemplate.getId()));

        if (existingPatientWelcomeMessageTemplate != null) {
            return existingPatientWelcomeMessageTemplate;
        }

        final PatientTemplate newPatientTemplate = new PatientTemplateBuilder(welcomeMessageTemplate, patient).build();
        return patientTemplateService.saveOrUpdate(newPatientTemplate);
    }

    private Template getWelcomeMessageTemplate() {
        final List<Template> welcomeMessageTemplates =
                templateService.findAllByCriteria(TemplateCriteria.forName(CFLConstants.WELCOME_MESSAGE_TEMPLATE));
        return welcomeMessageTemplates.get(0);
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
}
