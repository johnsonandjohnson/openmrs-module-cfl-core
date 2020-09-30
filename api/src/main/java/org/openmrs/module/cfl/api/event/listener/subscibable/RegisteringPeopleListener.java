package org.openmrs.module.cfl.api.event.listener.subscibable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.jms.Message;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.EventMessage;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.event.params.CallEventParamsConstants;
import org.openmrs.module.cfl.api.event.params.SmsEventParamsConstants;

public class RegisteringPeopleListener extends PeopleActionListener {

  private static final String SEND_SMS = "send_sms";
  private static final String CALLFLOWS_INITIATE_CALL = "callflows-call-initiate";

  @Override
  public List<String> subscribeToActions() {
    return Collections.singletonList(Event.Action.CREATED.name());
  }

  @Override
  public void performAction(Message message) {
    Person person = extractPerson(message);
    if (person != null) {
      if (isSmsEnabled()) {
        sendSms(person);
      }
      if (isCallEnabled()) {
        performCall(person);
      }
    }
  }

  private boolean isSmsEnabled() {
    AdministrationService administrationService = Context.getService(AdministrationService.class);
    return Boolean.parseBoolean(administrationService
        .getGlobalProperty(CFLConstants.SEND_SMS_ON_PATIENT_REGISTRATION_KEY));
  }

  private void sendSms(Person person) {
    EventMessage message = new EventMessage();

    message.put(SmsEventParamsConstants.CONFIG, "config");
    message.put(SmsEventParamsConstants.CUSTOM_PARAMS, new HashMap<String, String>());
    message.put(SmsEventParamsConstants.DELIVERY_TIME, new Date());
    message.put(SmsEventParamsConstants.MESSAGE, getWelcomeMessage(person));
    message.put(SmsEventParamsConstants.OPENMRS_ID, "cfl_id");
    message.put(SmsEventParamsConstants.PROVIDER_MESSAGE_ID, "provider_id");
    message.put(SmsEventParamsConstants.RECIPIENTS,
        new ArrayList<String>(Collections.singletonList(getPhoneNumber(person))));

    Event.fireEvent(SEND_SMS, message);
  }

  private String getWelcomeMessage(Person person) {
    return "Hello " + person.getPersonName().getFullName() + " you are registered.";
  }

  private boolean isCallEnabled() {
    AdministrationService administrationService = Context.getService(AdministrationService.class);
    return Boolean.parseBoolean(administrationService
        .getGlobalProperty(CFLConstants.PERFORM_CALL_ON_PATIENT_REGISTRATION_KEY));
  }

  private void performCall(Person person) {
    EventMessage message = new EventMessage();

    message.put(CallEventParamsConstants.PARAM_CONFIG, "config");
    message.put(CallEventParamsConstants.PARAM_FLOW_NAME, getCallFlowName());
    message.put(CallEventParamsConstants.PARAM_PHONE, getPhoneNumber(person));

    Event.fireEvent(CALLFLOWS_INITIATE_CALL, message);
  }

  private String getCallFlowName() {
    AdministrationService administrationService = Context.getService(AdministrationService.class);
    return administrationService.getGlobalProperty(CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY);
  }

  private String getPhoneNumber(Person person) {
    return person.getAttribute(CFLConstants.TELEPHONE_ATTRIBUTE_NAME).getValue();
  }
}
