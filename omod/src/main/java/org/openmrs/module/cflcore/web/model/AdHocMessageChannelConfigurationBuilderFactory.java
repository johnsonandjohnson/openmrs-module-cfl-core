package org.openmrs.module.cflcore.web.model;

import org.openmrs.api.AdministrationService;
import org.openmrs.module.cflcore.api.dto.AdHocMessageRequestDTO;
import org.openmrs.module.cflcore.api.util.GlobalPropertiesConstants;

public class AdHocMessageChannelConfigurationBuilderFactory {
  private AdHocMessageChannelConfigurationBuilderFactory() {}

  public static AdHocMessageChannelConfigurationBuilder builderFor(
      AdministrationService administrationService, AdHocMessageControllerModel model) {
    final AdHocMessageRequestDTO messageRequestDTO = model.getMessageRequest();

    if (Boolean.TRUE.equals(messageRequestDTO.getCallChannel())) {
      return new AdHocMessageChannelConfigurationCallBuilder(messageRequestDTO);
    } else if (Boolean.TRUE.equals(messageRequestDTO.getSmsChannel())) {
      return new AdHocMessageChannelConfigurationTextBuilder(
          administrationService,
          messageRequestDTO,
          GlobalPropertiesConstants.AD_HOC_SMS_MESSAGE_TEMPLATE.getKey());
    } else if (Boolean.TRUE.equals(messageRequestDTO.getWhatsAppChannel())) {
      return new AdHocMessageChannelConfigurationTextBuilder(
          administrationService,
          messageRequestDTO,
          GlobalPropertiesConstants.AD_HOC_WHATS_APP_MESSAGE_TEMPLATE.getKey());
    } else {
      throw new IllegalStateException(
          String.format(
              "AdHoc message request for delivery datetime '%s' doesn't contain "
                  + "either Callflow, SMS or WhatsApp message.",
              model.getMessageRequest().getDeliveryDateTime().toString()));
    }
  }
}
