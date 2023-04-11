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

import org.openmrs.api.AdministrationService;
import org.openmrs.module.cflcore.api.dto.AdHocMessageRequestDTO;
import org.openmrs.module.cflcore.api.util.GlobalPropertiesConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class AdHocMessageChannelConfigurationBuilderFactory {
  private AdHocMessageChannelConfigurationBuilderFactory() {}

  public static AdHocMessageChannelConfigurationBuilder builderFor(
      AdministrationService administrationService, AdHocMessageControllerModel model) {
    final AdHocMessageRequestDTO messageRequestDTO = model.getMessageRequest();
    final List<AdHocMessageChannelConfigurationBuilder> builders = new ArrayList<>();

    if (Boolean.TRUE.equals(messageRequestDTO.getCallChannel())) {
      builders.add(new AdHocMessageChannelConfigurationCallBuilder(messageRequestDTO));
    }
    if (Boolean.TRUE.equals(messageRequestDTO.getSmsChannel())) {
      builders.add(
          new AdHocMessageChannelConfigurationTextBuilder(
              administrationService,
              messageRequestDTO,
              GlobalPropertiesConstants.AD_HOC_SMS_MESSAGE_TEMPLATE.getKey()));
    }
    if (Boolean.TRUE.equals(messageRequestDTO.getWhatsAppChannel())) {
      builders.add(
          new AdHocMessageChannelConfigurationTextBuilder(
              administrationService,
              messageRequestDTO,
              GlobalPropertiesConstants.AD_HOC_WHATS_APP_MESSAGE_TEMPLATE.getKey()));
    }
    if (builders.isEmpty()) {
      throw new IllegalStateException(
          String.format(
              "AdHoc message request for delivery datetime '%s' doesn't contain "
                  + "either Callflow, SMS or WhatsApp message.",
              Optional.ofNullable(model.getMessageRequest().getDeliveryDateTime())
                  .map(Date::toString)
                  .orElse("<not set>")));
    }

    return new AdHocMessageChannelConfigurationListBuilder(builders);
  }
}
