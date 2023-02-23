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
    final String testStringAsJson = "Hello, \\\" \\n \\r \\\\";

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
