/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.dto;

import org.openmrs.module.cflcore.CFLConstants;
import org.springframework.util.AutoPopulatingList;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdHocMessageRequestDTO {

    private Date deliveryDateTime;
    private Boolean callChannel;
    private Boolean smsChannel;
    private Boolean whatsAppChannel;
    private String callFlowName;
    private String message;
    private List<AdHocMessagePatientFilterDTO> filters;

    public AdHocMessageRequestDTO() {
        this.filters = new AutoPopulatingList<>(AdHocMessagePatientFilterDTO.class);
    }

    public void applyFromOther(AdHocMessageRequestDTO otherDto) {
        setDeliveryDateTime(otherDto.getDeliveryDateTime());
        setCallChannel(otherDto.getCallChannel());
        setSmsChannel(otherDto.getSmsChannel());
        setWhatsAppChannel(otherDto.getWhatsAppChannel());
        setCallFlowName(otherDto.getCallFlowName());
        setMessage(otherDto.getMessage());

        for (int filterIdx = 0; filterIdx < getFilters().size(); ++filterIdx) {
            getFilters().get(filterIdx).copyValue(otherDto.getFilters().get(filterIdx));
        }
    }

    public Set<String> getChannels() {
        final Set<String> result = new HashSet<>();
        if (Boolean.TRUE.equals(callChannel)) {
            result.add(CFLConstants.CALL_CHANNEL_TYPE);
        }
        if (Boolean.TRUE.equals(smsChannel)) {
            result.add(CFLConstants.SMS_CHANNEL_TYPE);
        }
        if (Boolean.TRUE.equals(whatsAppChannel)) {
            result.add(CFLConstants.WHATSAPP_CHANNEL_TYPE);
        }

        return result;
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

    public Boolean getWhatsAppChannel() {
        return whatsAppChannel;
    }

    public void setWhatsAppChannel(Boolean whatsAppChannel) {
        this.whatsAppChannel = whatsAppChannel;
    }

    public String getCallFlowName() {
        return callFlowName;
    }

    public void setCallFlowName(String callFlowName) {
        this.callFlowName = callFlowName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AdHocMessagePatientFilterDTO> getFilters() {
        return filters;
    }

    public void setFilters(List<AdHocMessagePatientFilterDTO> filters) {
        this.filters = filters;
    }
}
