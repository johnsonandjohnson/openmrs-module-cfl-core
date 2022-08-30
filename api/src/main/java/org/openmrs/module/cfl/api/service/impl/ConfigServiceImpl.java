package org.openmrs.module.cfl.api.service.impl;

import static org.openmrs.module.cfl.CFLConstants.VACCINATION_PROGRAM_KEY;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.constant.ConfigConstants;
import org.openmrs.module.cfl.api.contract.countrysettings.CountrySettings;
import org.openmrs.module.cfl.api.contract.countrysettings.AllCountrySettings;
import org.openmrs.module.cfl.api.contract.countrysettings.Settings;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.copied.messages.model.RelationshipTypeDirection;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.strategy.FindPersonFilterStrategy;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.module.cfl.api.util.GlobalPropertyUtils;

public class ConfigServiceImpl implements ConfigService {

    @Override
    public String getActorTypesConfiguration() {
        return getGp(CFLConstants.ACTOR_TYPES_KEY);
    }

    @Override
    public String getDefaultActorRelationDirection() {
        return RelationshipTypeDirection.A.name();
    }

    @Override
    public FindPersonFilterStrategy getPersonFilterStrategy() {
        String beanName = getGp(ConfigConstants.FIND_PERSON_FILTER_STRATEGY_KEY);
        if (StringUtils.isBlank(beanName)) {
            return null;
        }
        return Context.getRegisteredComponent(beanName, FindPersonFilterStrategy.class);
    }

    @Override
    public int getLastViewedPersonSizeLimit() {
        String gpName = ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_KEY;
        return GlobalPropertyUtils.parseInt(
                gpName,
                getGp(gpName, ConfigConstants.LAST_VIEWED_PATIENT_SIZE_LIMIT_DEFAULT_VALUE));
    }

    @Override
    public Randomization getRandomizationGlobalProperty() {
        return new Randomization(new Gson().fromJson(getGp(VACCINATION_PROGRAM_KEY), Vaccination[].class));
    }

    @Override
    public String getVaccinationProgram(Person person) {
        return person.getAttribute(CFLConstants.VACCINATION_PROGRAM_ATTRIBUTE_NAME).getValue();
    }

    @Override
    public boolean isVaccinationInfoIsEnabled() {
        return Boolean.parseBoolean(Context.getAdministrationService()
                .getGlobalProperty(CFLConstants.VACCINATION_INFORMATION_ENABLED_KEY));
    }

    @Override
    public String getDefaultUserTimeZone() {
        return getGp(CFLConstants.DEFAULT_USER_TIME_ZONE_GP_NAME, DateUtil.DEFAULT_SYSTEM_TIME_ZONE.getID());
    }

    @Override
    public Map<String, Settings> getCountrySettingMap(String globalPropertyName) {
        Gson gson = new Gson();
        String countrySettings = getGp(globalPropertyName);
        JsonArray jsonArray = gson.fromJson(countrySettings, JsonArray.class);
        Map<String, Settings> countrySettingMap = new HashMap<>();
        for (JsonElement jsonElement : jsonArray) {
            for (Map.Entry<String, JsonElement> en : jsonElement.getAsJsonObject().entrySet()) {
                countrySettingMap.put(en.getKey(), gson.fromJson(en.getValue().toString(), Settings.class));
            }
        }
        return countrySettingMap;
    }

    @Override
    public AllCountrySettings getAllCountrySettings() {
        return new AllCountrySettings(new Gson().fromJson(getGp(CFLConstants.COUNTRY_SETTINGS_MAP_KEY),
            CountrySettings[].class));
    }

    private String getGp(String propertyName) {
        return Context.getAdministrationService().getGlobalProperty(propertyName);
    }

    private String getGp(String propertyName, String defaultValue) {
        return Context.getAdministrationService().getGlobalProperty(propertyName, defaultValue);
    }
}
