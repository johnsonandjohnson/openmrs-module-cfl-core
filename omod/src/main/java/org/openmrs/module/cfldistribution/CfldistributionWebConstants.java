package org.openmrs.module.cfldistribution;

public final class CfldistributionWebConstants {

  public static final String MODULE_ID = "cfldistribution";

  /**
   * The name of Global Property with a name of User Property where user's location UUID is saved.
   * This property is created by Location Based Access Control module.
   */
  public static final String LOCATION_USER_PROPERTY_NAME =
      "referenceapplication.locationUserPropertyName";

  public static final String HOME_PAGE_EXTENSION_POINT_ID =
      "org.openmrs.cfldistribution.homepageLink";

  public static final String SESSION_ATTRIBUTE_INFO_MESSAGE =
      "_REFERENCE_APPLICATION_INFO_MESSAGE_";

  public static final String SESSION_ATTRIBUTE_ERROR_MESSAGE =
      "_REFERENCE_APPLICATION_ERROR_MESSAGE_";

  public static final String SESSION_ATTRIBUTE_REDIRECT_URL =
      "_REFERENCE_APPLICATION_REDIRECT_URL_";

  public static final String REQUEST_PARAMETER_NAME_REDIRECT_URL = "redirectUrl";

  public static final String COOKIE_NAME_LAST_SESSION_LOCATION =
      "cfldistribution.lastSessionLocation";

  public static final String COOKIE_NAME_LAST_USER = "_REFERENCE_APPLICATION_LAST_USER_";
}
