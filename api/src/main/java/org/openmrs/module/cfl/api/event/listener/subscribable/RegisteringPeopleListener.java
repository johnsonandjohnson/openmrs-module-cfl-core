package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.event.params.CallEventParamsConstants;
import org.openmrs.module.cfl.api.event.params.SmsEventParamsConstants;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.cfl.api.util.VisitUtil;
import org.openmrs.module.messages.api.service.PatientTemplateService;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.service.SmsService;

import javax.jms.Message;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class RegisteringPeopleListener extends PeopleActionListener {

  private static final String CALL_SERVICE_BEAN_NAME = "callService";
  private static final String SMS_SERVICE_BEAN_NAME = "sms.SmsService";
  private static final String PATIENT_TEMPLATE_SERVICE_BEAN_NAME = "messages.patientTemplateService";

  @Override
  public List<String> subscribeToActions() {
    return Collections.singletonList(Event.Action.CREATED.name());
  }

  @Override
  public void performAction(Message message) {
    String channel = null;
    Person person = extractPerson(message);
    if (person != null) {
      if (isSmsEnabled()) {
        sendSms(person);
        channel = "SMS";
      }
      if (isCallEnabled()) {
        performCall(person);
        channel = "Call";
      }

      createVisitReminder(channel, person.getUuid());
      createFirstVisit(person.getUuid(), getVaccinationProgram(person));
    }
  }

  private boolean isSmsEnabled() {
    return Boolean.parseBoolean(getAdministrationService()
        .getGlobalProperty(CFLConstants.SEND_SMS_ON_PATIENT_REGISTRATION_KEY));
  }

  private void sendSms(Person person) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(SmsEventParamsConstants.RECIPIENTS,
            new ArrayList<String>(Collections.singletonList(getPhoneNumber(person))));
    properties.put(SmsEventParamsConstants.MESSAGE, getWelcomeMessage());

    Context.getRegisteredComponent(SMS_SERVICE_BEAN_NAME, SmsService.class)
            .send(new OutgoingSms(new SmsEvent(properties)));
  }

  private String getWelcomeMessage() {
    return getAdministrationService().getGlobalProperty(CFLConstants.SMS_MESSAGE_AFTER_REGISTRATION_KEY);
  }

  private boolean isCallEnabled() {
    return Boolean.parseBoolean(getAdministrationService()
        .getGlobalProperty(CFLConstants.PERFORM_CALL_ON_PATIENT_REGISTRATION_KEY));
  }

  private void performCall(Person person) {
    Map<String, Object> additionalParams = new HashMap<String, Object>();
    additionalParams.put(CallEventParamsConstants.PARAM_ACTOR_TYPE, CallEventParamsConstants.PATIENT_ACTOR_TYPE);
    additionalParams.put(CallEventParamsConstants.PARAM_PHONE, getPhoneNumber(person));
    additionalParams.put(CallEventParamsConstants.PARAM_ACTOR_ID, person.getPersonId());

    Context.getRegisteredComponent(CALL_SERVICE_BEAN_NAME, CallService.class)
            .makeCall(getCallConfig(), getCallFlowName(), additionalParams);
  }

  private String getCallFlowName() {
    return getAdministrationService().getGlobalProperty(CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY);
  }

  private String getPhoneNumber(Person person) {
    return person.getAttribute(CFLConstants.TELEPHONE_ATTRIBUTE_NAME).getValue();
  }

  private String getVaccinationProgram(Person person) {
    return person.getAttribute(CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME).getValue();
  }

  private String getCallConfig() {
    return getAdministrationService().getGlobalProperty(CallEventParamsConstants.CALL_CONFIG,
            CallEventParamsConstants.CALL_CONFIG_DEFAULT_VALUE);
  }

  private void createVisitReminder(String channel, String patientUuid) {
    Context.getRegisteredComponent(PATIENT_TEMPLATE_SERVICE_BEAN_NAME, PatientTemplateService.class)
            .createVisitReminder(channel, patientUuid);
  }

  private void createFirstVisit(String patientUuid, String vaccinationProgram) {
    Vaccination[] vaccinations = getConfigService().getRandomizationGlobalProperty();

    Vaccination vaccination = Vaccination.findByVaccinationProgram(vaccinations, vaccinationProgram);
    VisitInformation visitInformation = vaccination.findByVisitName(CFLConstants.DOSE_1_VISIT_NAME);

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

    Context.getVisitService().saveVisit(visit);
  }

  private AdministrationService getAdministrationService() {
    return Context.getAdministrationService();
  }

  private ConfigService getConfigService() {
    return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
  }
}
