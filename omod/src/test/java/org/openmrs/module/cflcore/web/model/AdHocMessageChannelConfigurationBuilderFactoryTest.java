package org.openmrs.module.cflcore.web.model;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.cflcore.api.dto.AdHocMessageRequestDTO;
import org.openmrs.module.messages.api.service.impl.AbstractTextMessageServiceResultsHandlerService;
import org.openmrs.module.messages.api.service.impl.CallFlowServiceResultsHandlerServiceImpl;

import java.util.Map;

public class AdHocMessageChannelConfigurationBuilderFactoryTest {
  @Test
  public void shouldBuildConfigurationForMultipleChannels() {
    final AdministrationService administrationServiceMock =
        Mockito.mock(AdministrationService.class);
    Mockito.when(administrationServiceMock.getGlobalProperty(Mockito.anyString()))
        .thenReturn("##message##");

    final AdHocMessageControllerModel model = new AdHocMessageControllerModel();
    model.setMessageRequest(new AdHocMessageRequestDTO());
    model.getMessageRequest().setSmsChannel(true);
    model.getMessageRequest().setMessage("Hello SMS");
    model.getMessageRequest().setCallChannel(true);
    model.getMessageRequest().setCallFlowName("FlowName");

    final Map<String, String> channelConfiguration =
        AdHocMessageChannelConfigurationBuilderFactory.builderFor(administrationServiceMock, model)
            .build();

    Assert.assertNotNull(channelConfiguration);
    Assert.assertEquals(
        "\"Hello SMS\"",
        channelConfiguration.get(
            AbstractTextMessageServiceResultsHandlerService.MESSAGE_CHANNEL_CONF_TEMPLATE_VALUE));
    Assert.assertEquals(
        "FlowName",
        channelConfiguration.get(
            CallFlowServiceResultsHandlerServiceImpl.CALL_CHANNEL_CONF_FLOW_NAME));
  }
}
