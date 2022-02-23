package org.openmrs.module.cfl.api.util;

import org.openmrs.Concept;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.contract.CountrySettingBuilder;
import org.openmrs.module.messages.api.service.CountryPropertyService;
import org.openmrs.module.messages.api.util.PersonAddressUtil;

public final class CountrySettingUtil {

  public static String getChannelTypesForVisitReminder(Person person) {
    CountrySetting countrySetting = getCountrySettingForPatient(person);
    return getChannelTypes(countrySetting);
  }

  public static CountrySetting getCountrySettingForPatient(Person person) {
    final CountryPropertyService countryPropertyService = getCountryPropertyService();
    final Concept countryConcept = PersonAddressUtil.getPersonCountry(person).orElse(null);
    final CountrySettingBuilder countrySettingBuilder = new CountrySettingBuilder();

    for (String countryPropertyName : CountrySettingBuilder.ALL_PROP_NAMES) {
      countryPropertyService
          .getCountryProperty(countryConcept, countryPropertyName)
          .ifPresent(countrySettingBuilder::addCountryProperty);
    }

    return countrySettingBuilder.build();
  }

  private static String getChannelTypes(CountrySetting countrySetting) {
    String channel = "";
    if (countrySetting.isShouldSendReminderViaSms()
        && countrySetting.isShouldSendReminderViaCall()) {
      channel = CFLConstants.SMS_CHANNEL_TYPE.concat("," + CFLConstants.CALL_CHANNEL_TYPE);
    } else if (countrySetting.isShouldSendReminderViaCall()) {
      channel = CFLConstants.CALL_CHANNEL_TYPE;
    } else if (countrySetting.isShouldSendReminderViaSms()) {
      channel = CFLConstants.SMS_CHANNEL_TYPE;
    }
    return channel;
  }

  private static CountryPropertyService getCountryPropertyService() {
    return Context.getService(CountryPropertyService.class);
  }

  private CountrySettingUtil() {}
}
