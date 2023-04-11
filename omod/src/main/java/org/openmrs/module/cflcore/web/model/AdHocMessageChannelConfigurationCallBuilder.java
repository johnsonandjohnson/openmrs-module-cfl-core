/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.web.model;

import org.openmrs.module.cflcore.api.dto.AdHocMessageRequestDTO;
import org.openmrs.module.messages.api.service.impl.CallFlowServiceResultsHandlerServiceImpl;

import java.util.Collections;
import java.util.Map;

class AdHocMessageChannelConfigurationCallBuilder
    implements AdHocMessageChannelConfigurationBuilder {
  private AdHocMessageRequestDTO messageRequestDTO;

  AdHocMessageChannelConfigurationCallBuilder(AdHocMessageRequestDTO messageRequestDTO) {
    this.messageRequestDTO = messageRequestDTO;
  }

  @Override
  public Map<String, String> build() {
    return Collections.singletonMap(
        CallFlowServiceResultsHandlerServiceImpl.CALL_CHANNEL_CONF_FLOW_NAME,
        messageRequestDTO.getCallFlowName());
  }
}
