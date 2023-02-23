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
