package org.openmrs.module.cflcore.web.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.cflcore.api.dto.AdHocMessageRequestDTO;
import org.openmrs.module.messages.api.service.impl.AbstractTextMessageServiceResultsHandlerService;

public class AdHocMessageChannelConfigurationTextBuilderTest {
  @Test
  public void shouldEscapeSpacialCharactersInMessageText() throws JsonProcessingException {
    final String testString = "Hello, \" \n \r \\";
    final String testStringAsJson = "\"Hello, \\\" \\n \\r \\\\\"";

    final AdministrationService administrationServiceMock =
        Mockito.mock(AdministrationService.class);
    Mockito.when(administrationServiceMock.getGlobalProperty(Mockito.anyString()))
        .thenReturn("##message##");

    final AdHocMessageRequestDTO adHocMessageRequestDTO = new AdHocMessageRequestDTO();
    adHocMessageRequestDTO.setMessage(testString);

    final AdHocMessageChannelConfigurationTextBuilder builder =
        new AdHocMessageChannelConfigurationTextBuilder(
            administrationServiceMock, adHocMessageRequestDTO, "");

    String result =
        builder
            .build()
            .get(
                AbstractTextMessageServiceResultsHandlerService
                    .MESSAGE_CHANNEL_CONF_TEMPLATE_VALUE);

    Assert.assertEquals(testStringAsJson, result);
  }
}
