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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.cflcore.api.dto.AdHocMessageRequestDTO;
import org.openmrs.module.messages.api.service.impl.AbstractTextMessageServiceResultsHandlerService;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

class AdHocMessageChannelConfigurationTextBuilder
    implements AdHocMessageChannelConfigurationBuilder {
  private static final String MESSAGE_PLACEHOLDER = "##message##";

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final AdministrationService administrationService;
  private final AdHocMessageRequestDTO messageRequestDTO;
  private final String templateGPKey;

  AdHocMessageChannelConfigurationTextBuilder(
      AdministrationService administrationService,
      AdHocMessageRequestDTO messageRequestDTO,
      String templateGPKey) {
    this.administrationService = administrationService;
    this.messageRequestDTO = messageRequestDTO;
    this.templateGPKey = templateGPKey;
  }

  @Override
  public Map<String, String> build() {
    final String template = resolveMessageTemplateGP();
    return Collections.singletonMap(
        AbstractTextMessageServiceResultsHandlerService.MESSAGE_CHANNEL_CONF_TEMPLATE_VALUE,
        template);
  }

  private String resolveMessageTemplateGP() {
    final String templateText = administrationService.getGlobalProperty(templateGPKey);

    try {
      return templateText.replace(
          MESSAGE_PLACEHOLDER, objectMapper.writeValueAsString(messageRequestDTO.getMessage()));
    } catch (JsonProcessingException e) {
      throw new APIException(
          MessageFormat.format(
              "Failed to replace message: {0} in AdHoc template: {1}.",
              messageRequestDTO.getMessage(), templateText),
          e);
    }
  }
}
