package org.openmrs.module.cfl.api.dto;

import org.openmrs.module.messages.api.service.impl.CallFlowServiceResultsHandlerServiceImpl;
import org.openmrs.module.messages.api.service.impl.SmsServiceResultsHandlerServiceImpl;
import org.springframework.util.AutoPopulatingList;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdHocMessageRequestDTO {
    private static final String MESSAGE_TEMPLATE_DEFAULT = "{ \"message\":\"Simple message here.\" }";

    private Date deliveryDateTime;
    private Boolean callChannel;
    private Boolean smsChannel;
    private String callFlowName;
    private String messageTemplate;
    private List<AdHocMessagePatientFilterDTO> filters;

    public AdHocMessageRequestDTO() {
        this.messageTemplate = MESSAGE_TEMPLATE_DEFAULT;
        this.filters = new AutoPopulatingList<AdHocMessagePatientFilterDTO>(AdHocMessagePatientFilterDTO.class);
    }

    public void applyFromOther(AdHocMessageRequestDTO otherDto) {
        setDeliveryDateTime(otherDto.getDeliveryDateTime());
        setCallChannel(otherDto.getCallChannel());
        setSmsChannel(otherDto.getSmsChannel());
        setCallFlowName(otherDto.getCallFlowName());
        setMessageTemplate(otherDto.getMessageTemplate());

        for (int filterIdx = 0; filterIdx < getFilters().size(); ++filterIdx) {
            getFilters().get(filterIdx).copyValue(otherDto.getFilters().get(filterIdx));
        }
    }

    public Set<String> getChannels() {
        final Set<String> result = new HashSet<String>();
        if (Boolean.TRUE.equals(callChannel)) {
            result.add("Call");
        }
        if (Boolean.TRUE.equals(smsChannel)) {
            result.add("SMS");
        }
        return result;
    }

    public Map<String, String> getProperties() {
        final Map<String, String> properties = new HashMap<String, String>();
        if (Boolean.TRUE.equals(callChannel)) {
            properties.put(CallFlowServiceResultsHandlerServiceImpl.CALL_CHANNEL_CONF_FLOW_NAME, callFlowName);
        }
        if (Boolean.TRUE.equals(smsChannel)) {
            properties.put(SmsServiceResultsHandlerServiceImpl.SMS_CHANNEL_CONF_TEMPLATE_VALUE, messageTemplate);
        }
        return properties;
    }

    public Date getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public void setDeliveryDateTime(Date deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
    }

    public Boolean getCallChannel() {
        return callChannel;
    }

    public void setCallChannel(Boolean callChannel) {
        this.callChannel = callChannel;
    }

    public Boolean getSmsChannel() {
        return smsChannel;
    }

    public void setSmsChannel(Boolean smsChannel) {
        this.smsChannel = smsChannel;
    }

    public String getCallFlowName() {
        return callFlowName;
    }

    public void setCallFlowName(String callFlowName) {
        this.callFlowName = callFlowName;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public List<AdHocMessagePatientFilterDTO> getFilters() {
        return filters;
    }

    public void setFilters(List<AdHocMessagePatientFilterDTO> filters) {
        this.filters = filters;
    }
}
