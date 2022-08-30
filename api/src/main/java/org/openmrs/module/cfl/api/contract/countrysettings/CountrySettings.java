package org.openmrs.module.cfl.api.contract.countrysettings;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.cfl.CFLConstants;

public class CountrySettings {

  private String countryName;

  private List<LanguageSettings> languageCountrySettingMap;

  public Optional<LanguageSettings> findByLanguage(String language) {
    return languageCountrySettingMap.stream()
        .filter(obj -> StringUtils.equalsIgnoreCase(obj.getLanguage(), language)).findFirst();
  }

  public Optional<LanguageSettings> getDefaultLanguageCountrySettingsMap() {
    return findByLanguage(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY);
  }

  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public List<LanguageSettings> getLanguageCountrySettingMap() {
    return languageCountrySettingMap;
  }

  public void setLanguageCountrySettingMap(
      List<LanguageSettings> languageCountrySettingMap) {
    this.languageCountrySettingMap = languageCountrySettingMap;
  }
}
