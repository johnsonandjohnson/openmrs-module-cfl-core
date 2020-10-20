package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.apache.commons.lang.StringUtils;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.openmrs.module.messages.api.constants.ConfigConstants;
import org.openmrs.module.messages.api.constants.MessagesConstants;
import org.openmrs.module.messages.api.event.CallFlowParamConstants;
import org.openmrs.module.messages.api.event.MessagesEvent;
import org.openmrs.module.messages.api.event.SmsEventParamConstants;
import org.openmrs.module.messages.api.service.MessagesEventService;
import org.openmrs.module.messages.api.service.PatientTemplateService;

import javax.jms.Message;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.openmrs.module.messages.api.event.CallFlowParamConstants.ADDITIONAL_PARAMS;
import static org.openmrs.module.messages.api.event.CallFlowParamConstants.CONFIG;
import static org.openmrs.module.messages.api.event.CallFlowParamConstants.FLOW_NAME;

public class RegisteringPeopleListener extends PeopleActionListener {

  private static final String SMS_CHANNEL = "SMS";
  private static final String CALL_CHANNEL = "Call";
  private static final String PATIENT_TEMPLATE_SERVICE_BEAN_NAME = "messages.patientTemplateService";
  private static final String MESSAGES_EVENT_SERVICE_BEAN_NAME = "messages.messagesEventService";
  private static final String SMS_INITIATE_EVENT = "send_sms";
  private static final String PATIENT_ACTOR_TYPE = "patient";
  private static final String HOUR_MINUTES_SEPARATOR = ":";

  @Override
  public List<String> subscribeToActions() {
    return Collections.singletonList(Event.Action.CREATED.name());
  }

  @Override
  public void performAction(Message message) {
    Person person = extractPerson(message);
      if (person != null) {
        sendWelcomeMessages(person);
        if (getConfigService().isVaccinationInfoIsEnabled()) {
          createFirstVisit(person.getUuid(), getConfigService().getVaccinationProgram(person));
          performActionsAfterPatientRegistration(person);
          updateGlobalProperty(getConfigService().getRefreshDate(person));
        }
      }
    }

  private void performActionsAfterPatientRegistration(Person person) {
    if (StringUtils.isNotBlank(getPhoneNumber(person)) && StringUtils.isNotBlank(getChannelTypesForMessages())) {
        createVisitReminder(getChannelTypesForMessages(), person.getUuid());
    }
  }

  private void sendWelcomeMessages(Person person) {
    if (StringUtils.isNotBlank(getPhoneNumber(person))) {
      if (isSmsEnabled()) {
        sendSms(person);
      }
      if (isCallEnabled()) {
        performCall(person);
      }
    }
  }

  private void updateGlobalProperty(String refreshDate) {
    if (StringUtils.isNotEmpty(refreshDate)) {
      long lastSaved = Long.parseLong(getConfigService().getLastPatientRefreshDate());
      long patientRefreshDate = Long.parseLong(refreshDate);
      if (patientRefreshDate > lastSaved) {
        getConfigService().setLastPatientRefreshDate(Long.toString(patientRefreshDate));
      }
    }
  }

  private String getChannelTypesForMessages() {
    String channel = "";
    if (isReminderViaSmsIsEnabled() && isReminderViaCallIsEnabled()) {
      channel = SMS_CHANNEL.concat("," + CALL_CHANNEL);
    } else if (isReminderViaCallIsEnabled()) {
      channel = CALL_CHANNEL;
    } else if (isReminderViaSmsIsEnabled()) {
      channel = SMS_CHANNEL;
    }
    return channel;
  }

  private boolean isSmsEnabled() {
    return Boolean.parseBoolean(getAdministrationService()
        .getGlobalProperty(CFLConstants.SEND_SMS_ON_PATIENT_REGISTRATION_KEY));
  }

  private boolean isReminderViaSmsIsEnabled() {
    return Boolean.parseBoolean(getAdministrationService()
            .getGlobalProperty(CFLConstants.SEND_REMINDER_VIA_SMS_KEY));
  }

  private boolean isReminderViaCallIsEnabled() {
    return Boolean.parseBoolean(getAdministrationService()
            .getGlobalProperty(CFLConstants.SEND_REMINDER_VIA_CALL_KEY));
  }

  private void sendSms(Person person) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(SmsEventParamConstants.RECIPIENTS,
            new ArrayList<String>(Collections.singletonList(getPhoneNumber(person))));
    properties.put(SmsEventParamConstants.MESSAGE, getWelcomeMessage());

    Context.getRegisteredComponent(MESSAGES_EVENT_SERVICE_BEAN_NAME, MessagesEventService.class)
            .sendEventMessage(new MessagesEvent(SMS_INITIATE_EVENT, properties));
  }

  private String getWelcomeMessage() {
    return getAdministrationService().getGlobalProperty(CFLConstants.SMS_MESSAGE_AFTER_REGISTRATION_KEY);
  }

  private boolean isCallEnabled() {
    return Boolean.parseBoolean(getAdministrationService()
        .getGlobalProperty(CFLConstants.PERFORM_CALL_ON_PATIENT_REGISTRATION_KEY));
  }

  private void performCall(Person person) {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put(CONFIG, getCallConfig());
    params.put(FLOW_NAME, getCallFlowName());

    Map<String, Object> additionalParams = new HashMap<String, Object>();
    additionalParams.put(CallFlowParamConstants.ACTOR_TYPE, PATIENT_ACTOR_TYPE);
    additionalParams.put(CallFlowParamConstants.PHONE, getPhoneNumber(person));
    additionalParams.put(CallFlowParamConstants.ACTOR_ID, person.getPersonId().toString());
    additionalParams.put(CallFlowParamConstants.REF_KEY, person.getPersonId().toString());

    params.put(ADDITIONAL_PARAMS, additionalParams);

    Context.getRegisteredComponent(MESSAGES_EVENT_SERVICE_BEAN_NAME, MessagesEventService.class)
            .sendEventMessage(new MessagesEvent(MessagesConstants.CALL_FLOW_INITIATE_CALL_EVENT, params));
  }

  private String getCallFlowName() {
    return getAdministrationService().getGlobalProperty(CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY);
  }

  private String getPhoneNumber(Person person) {
    PersonAttribute phoneAttribute = person.getAttribute(CFLConstants.TELEPHONE_ATTRIBUTE_NAME);
    if (phoneAttribute == null ||
            phoneAttribute.getValue().equals("-")) {
      return "";
    } else {
      return phoneAttribute.getValue();
    }
  }

  private String getCallConfig() {
    return getAdministrationService().getGlobalProperty(ConfigConstants.CALL_CONFIG,
            ConfigConstants.CALL_CONFIG_DEFAULT_VALUE);
  }

  private void createVisitReminder(String channel, String patientUuid) {
      saveBestContactTimeForPatient(patientUuid);
      Context.getRegisteredComponent(PATIENT_TEMPLATE_SERVICE_BEAN_NAME, PatientTemplateService.class)
              .createVisitReminder(channel, patientUuid);
  }

  private void saveBestContactTimeForPatient(String patientUuid) {
    Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

    if (patient != null && patient.getPatientIdentifier() != null) {
      Location patientLocation = patient.getPatientIdentifier().getLocation();
      if (patientLocation != null) {
        String patientTimeZone = getTimeZoneFromLocation(patientLocation);

        PersonAttribute bestContactTimeAttribute = new PersonAttribute();
        bestContactTimeAttribute.setAttributeType(getBestContactTimeAttrType());
        bestContactTimeAttribute.setValue(getBestContactTimeInProperFormat(patientTimeZone));
        patient.addAttribute(bestContactTimeAttribute);

        Context.getPatientService().savePatient(patient);
      }
    }
  }

  private String getBestContactTimeInProperFormat(String timeZone) {
    String defaultBestContactTime = getConfigService().getDefaultBestContactTime();
    String[] splittedDefaultBestContactTime = defaultBestContactTime.split(HOUR_MINUTES_SEPARATOR);

    if (StringUtils.isBlank(timeZone)) {
      return "";
    } else {
      Calendar localTime = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
      localTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splittedDefaultBestContactTime[0]));
      localTime.set(Calendar.MINUTE, Integer.parseInt(splittedDefaultBestContactTime[1]));
      SimpleDateFormat sdf = new SimpleDateFormat(CFLConstants.BEST_CONTACT_TIME_FORMAT);
      sdf.setCalendar(localTime);

      Calendar defaultTime = Calendar.getInstance(TimeZone.getDefault());
      defaultTime.setTime(localTime.getTime());
      sdf.setTimeZone(TimeZone.getDefault());

      return sdf.format(defaultTime.getTime());
    }
  }

  private String getTimeZoneFromLocation(Location location) {
    String patientTimeZone = "";
    Collection<LocationAttribute> locationAttributes = location.getActiveAttributes();
    for (LocationAttribute locationAttribute : locationAttributes) {
      if (locationAttribute.getAttributeType().equals(getPatientTimeZoneAttrType())) {
        patientTimeZone = locationAttribute.getValueReference();
        break;
      }
    }
    return patientTimeZone;
  }

  private void createFirstVisit(String patientUuid, String vaccinationProgram) {
    Randomization randomization = getConfigService().getRandomizationGlobalProperty();

    Vaccination vaccination = randomization.findByVaccinationProgram(vaccinationProgram);
    VisitInformation visitInformation = vaccination.getVisits().get(0);

    Visit visit = new Visit();
    visit.setPatient(Context.getPatientService().getPatientByUuid(patientUuid));
    visit.setStartDatetime(DateUtil.addDaysToDate(DateUtil.now(), visitInformation.getMidPointWindow()));
    visit.setVisitType(VisitUtil.getProperVisitType(visitInformation));

    VisitAttributeType visitAttributeType =
            Context.getVisitService().getVisitAttributeTypeByUuid(CFLConstants.VISIT_STATUS_ATTRIBUTE_TYPE_UUID);
    VisitAttribute visitAttribute = new VisitAttribute();
    visitAttribute.setAttributeType(visitAttributeType);
    visitAttribute.setValueReferenceInternal(CFLConstants.SCHEDULED_VISIT_STATUS);
    visit.setAttribute(visitAttribute);

    visit = VisitUtil.addVisitInformation(visit, visitInformation);

    Context.getVisitService().saveVisit(visit);
  }

  private AdministrationService getAdministrationService() {
    return Context.getAdministrationService();
  }

  private ConfigService getConfigService() {
    return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
  }

  private LocationAttributeType getPatientTimeZoneAttrType() {
    return Context.getLocationService()
            .getLocationAttributeTypeByName(CFLConstants.PATIENT_TIMEZONE_LOCATION_ATTR_TYPE_NAME);
  }

  private PersonAttributeType getBestContactTimeAttrType() {
    return Context.getPersonService()
            .getPersonAttributeTypeByUuid(CFLConstants.BEST_CONTACT_TIME_ATTRIBUTE_TYPE_UUID);
  }
}
