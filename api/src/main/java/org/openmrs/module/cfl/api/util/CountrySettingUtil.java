package org.openmrs.module.cfl.api.util;

import java.util.Optional;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.countrysettings.CountrySettings;
import org.openmrs.module.cfl.api.contract.countrysettings.AllCountrySettings;
import org.openmrs.module.cfl.api.contract.countrysettings.Settings;
import org.openmrs.module.cfl.api.contract.countrysettings.LanguageSettings;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CountrySettingUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(CountrySettingUtil.class);

  public static String getChannelTypesForVisitReminder(Person person) {
    Settings settings = getSettingsForPatient(person);
    return getChannelTypes(settings);
  }

  public static Settings getSettingsForPatient(Person person) {
    Patient patient = Context.getPatientService().getPatient(person.getPersonId());
    String patientCountry = PatientUtil.getPatientCountry(patient);
    String patientLanguage = PersonUtil.getPersonLanguage(person);
    CountrySettings countrySettings = findPatientCountrySettings(patientCountry);

    Settings settings = findPatientSettings(countrySettings, patientLanguage);
    if (settings == null) {
      throw new CflRuntimeException(
          String.format("Cannot find settings for country: %s and language: %s", patientCountry,
              patientLanguage));
    }

    return settings;
  }

  public static CountrySettings findPatientCountrySettings(String patientCountry) {
    AllCountrySettings allCountrySettings = getConfigService().getAllCountrySettings();
    Optional<CountrySettings> countrySettings = allCountrySettings.findByCountryName(
        patientCountry);
    if (countrySettings.isPresent()) {
      return countrySettings.get();
    } else {
      LOGGER.warn(
          String.format("Country settings for country: %s not found. Default config will be used",
              patientCountry));
      Optional<CountrySettings> defaultCountrySettings = allCountrySettings.getDefaultCountrySettings();
      if (defaultCountrySettings.isPresent()) {
        return defaultCountrySettings.get();
      } else {
        throw new IllegalStateException("Country settings for default country not found");
      }
    }
  }

  private static Settings findPatientSettings(CountrySettings countrySettings,
      String patientLanguage) {
    Optional<LanguageSettings> languageSettings = countrySettings.findByLanguage(
        patientLanguage);
    if (languageSettings.isPresent()) {
      return languageSettings.get().getSettings();
    } else {
      LOGGER.warn(String.format(
          "Language-country settings for language: %s not found. Default config will be used",
          patientLanguage));
      Optional<LanguageSettings> defaultLanguageSettings =
          countrySettings.getDefaultLanguageCountrySettingsMap();
      if (defaultLanguageSettings.isPresent()) {
        return defaultLanguageSettings.get().getSettings();
      } else {
        throw new IllegalStateException("Language settings for default language not found");
      }
    }
  }

  private static String getChannelTypes(Settings settings) {
    String channel = "";
    if (settings.isShouldSendReminderViaSms()
        && settings.isShouldSendReminderViaCall()) {
      channel = CFLConstants.SMS_CHANNEL_TYPE.concat("," + CFLConstants.CALL_CHANNEL_TYPE);
    } else if (settings.isShouldSendReminderViaCall()) {
      channel = CFLConstants.CALL_CHANNEL_TYPE;
    } else if (settings.isShouldSendReminderViaSms()) {
      channel = CFLConstants.SMS_CHANNEL_TYPE;
    }
    return channel;
  }

  private static ConfigService getConfigService() {
    return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME,
        ConfigService.class);
  }

  private CountrySettingUtil() {
  }
}
