package org.openmrs.module.cfl.web.model;

import org.openmrs.module.cfl.api.dto.PersonDTO;
import org.openmrs.module.cfl.api.dto.AdHocMessageRequestDTO;

import java.util.List;

/**
 * The AdHocMessageControllerModel Class.
 */
public class AdHocMessageControllerModel {
    private AdHocMessageRequestDTO messageRequest;
    private List<PersonDTO> recipients;

    public AdHocMessageControllerModel() {

    }

    public AdHocMessageControllerModel(AdHocMessageRequestDTO messageRequest, List<PersonDTO> recipients) {
        this.messageRequest = messageRequest;
        this.recipients = recipients;
    }

    public AdHocMessageRequestDTO getMessageRequest() {
        return messageRequest;
    }

    public void setMessageRequest(AdHocMessageRequestDTO messageRequest) {
        this.messageRequest = messageRequest;
    }

    public List<PersonDTO> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<PersonDTO> recipients) {
        this.recipients = recipients;
    }
}
