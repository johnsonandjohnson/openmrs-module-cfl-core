package org.openmrs.module.cfl.api.contract.countrysettings;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.cfl.CFLConstants;

public class AllCountrySettings {

  private List<CountrySettings> countrySettings;

  public AllCountrySettings(CountrySettings[] countrySettings) {
    this.countrySettings = Arrays.asList(countrySettings);
  }

  public Optional<CountrySettings> findByCountryName(String countryName) {
    return countrySettings.stream()
        .filter(config -> StringUtils.equalsIgnoreCase(config.getCountryName(), countryName))
        .findFirst();
  }

  public Optional<CountrySettings> getDefaultCountrySettings() {
    return findByCountryName(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY);
  }

  public List<CountrySettings> getCountryConfigs() {
    return countrySettings;
  }

  public void setCountryConfigs(
      List<CountrySettings> countrySettings) {
    this.countrySettings = countrySettings;
  }
}
