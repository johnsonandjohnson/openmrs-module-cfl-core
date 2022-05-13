/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
