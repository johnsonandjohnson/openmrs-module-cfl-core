package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.service.WelcomeService;
import org.openmrs.module.cfl.api.util.CountrySettingUtil;
import org.openmrs.module.cfl.api.util.PersonUtil;
import org.openmrs.module.messages.api.constants.MessagesConstants;
import org.openmrs.module.messages.api.event.CallFlowParamConstants;
import org.openmrs.module.messages.api.event.MessagesEvent;
import org.openmrs.module.messages.api.event.SmsEventParamConstants;
import org.openmrs.module.messages.api.service.MessagesEventService;
import org.openmrs.module.messages.api.service.impl.VelocityNotificationTemplateServiceImpl;
import org.openmrs.module.messages.api.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.openmrs.module.messages.api.event.CallFlowParamConstants.ADDITIONAL_PARAMS;
import static org.openmrs.module.messages.api.event.CallFlowParamConstants.CONFIG;
import static org.openmrs.module.messages.api.event.CallFlowParamConstants.FLOW_NAME;

public class WelcomeServiceImpl implements WelcomeService {

    private static final String MESSAGES_EVENT_SERVICE_BEAN_NAME = "messages.messagesEventService";
    private static final String VELOCITY_NOTIFICATION_TEMPLATE_SERVICE_BEAN_NAME =
            "messages.velocityNotificationTemplateServiceImpl";
    private static final String SMS_INITIATE_EVENT = "send_sms";
    private static final String PATIENT_ACTOR_TYPE = "patient";
    private static final String PATIENT_PARAM = "patient";
    private static final String SMS_CONFIG_KEY = "config";

    @Override
    public void sendWelcomeMessages(Person person) {
        CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(person);
        if (StringUtils.isNotBlank(PersonUtil.getPhoneNumber(person))) {
            if (countrySetting.isSendSmsOnPatientRegistration()) {
                sendSms(person, countrySetting.getSms());
            }
            if (countrySetting.isPerformCallOnPatientRegistration()) {
                performCall(person, countrySetting.getCall());
            }
        }
    }

    private void sendSms(Person person, String configName) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SmsEventParamConstants.RECIPIENTS,
                new ArrayList<String>(Collections.singletonList(PersonUtil.getPhoneNumber(person))));

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(PATIENT_PARAM, Context.getPatientService().getPatient(person.getPersonId()));

        Map<String, String> templateMap = JsonUtil.toMap(getParsedTemplate(param), JsonUtil.STRING_TO_STRING_MAP);
        String message = templateMap.get(SmsEventParamConstants.MESSAGE);

        Map<String, String> smsServiceParameters = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : templateMap.entrySet()) {
            String key = entry.getKey();
            if (!key.equals(SmsEventParamConstants.MESSAGE)) {
                smsServiceParameters.put(key, entry.getValue());
            }
        }

        properties.put(SmsEventParamConstants.MESSAGE, message);
        properties.put(SmsEventParamConstants.CUSTOM_PARAMS, smsServiceParameters);
        properties.put(SMS_CONFIG_KEY, configName);

        Context.getRegisteredComponent(MESSAGES_EVENT_SERVICE_BEAN_NAME, MessagesEventService.class)
                .sendEventMessage(new MessagesEvent(SMS_INITIATE_EVENT, properties));
    }

    private String getParsedTemplate(Map<String, Object> param) {
        return Context.getRegisteredComponent(VELOCITY_NOTIFICATION_TEMPLATE_SERVICE_BEAN_NAME,
                VelocityNotificationTemplateServiceImpl.class)
                .buildMessageByGlobalProperty(param, CFLConstants.SMS_MESSAGE_AFTER_REGISTRATION_KEY);
    }

    private void performCall(Person person, String configName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CONFIG, configName);
        params.put(FLOW_NAME, getCallFlowName());

        Map<String, Object> additionalParams = new HashMap<String, Object>();
        additionalParams.put(CallFlowParamConstants.ACTOR_TYPE, PATIENT_ACTOR_TYPE);
        additionalParams.put(CallFlowParamConstants.PHONE, PersonUtil.getPhoneNumber(person));
        additionalParams.put(CallFlowParamConstants.ACTOR_ID, person.getPersonId().toString());
        additionalParams.put(CallFlowParamConstants.REF_KEY, person.getPersonId().toString());

        params.put(ADDITIONAL_PARAMS, additionalParams);

        Context.getRegisteredComponent(MESSAGES_EVENT_SERVICE_BEAN_NAME, MessagesEventService.class)
                .sendEventMessage(new MessagesEvent(MessagesConstants.CALL_FLOW_INITIATE_CALL_EVENT, params));
    }

    private String getCallFlowName() {
        return getAdministrationService().getGlobalProperty(CFLConstants.PATIENT_REGISTRATION_CALL_FLOW_NAME_KEY);
    }

    private AdministrationService getAdministrationService() {
        return Context.getAdministrationService();
    }
}
