package org.openmrs.module.cfl.api.contract.countrysettings;

public class LanguageSettings {

  private String language;

  private Settings settings;

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public Settings getSettings() {
    return settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }
}
