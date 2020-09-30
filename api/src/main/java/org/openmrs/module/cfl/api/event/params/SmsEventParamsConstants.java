package org.openmrs.module.cfl.api.event.params;

public final class SmsEventParamsConstants {

  private SmsEventParamsConstants() {
  }

  /**
   * Config that was used for this message
   */
  public static final String CONFIG = "config";

  /**
   * list of recipients (phone numbers)
   */
  public static final String RECIPIENTS = "recipients";

  /**
   * time at which this SMS should be sent
   */
  public static final String DELIVERY_TIME = "delivery_time";

  /**
   * the text content of the SMS message
   */
  public static final String MESSAGE = "message";

  /**
   * OpenMRS unique message id
   */
  public static final String OPENMRS_ID = "openmrs_id";
  /**
   * provider unique message id
   */
  public static final String PROVIDER_MESSAGE_ID = "provider_message_id";

  /**
   * map of custom parameters
   */
  public static final String CUSTOM_PARAMS = "custom_params";
}
