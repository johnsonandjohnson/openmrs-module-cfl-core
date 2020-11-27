package org.openmrs.module.cfl.api.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.service.ConfigService;

import java.util.Map;

public final class CountrySettingUtil {

    public static String getChannelTypesForVisitReminder(Person person) {
        CountrySetting countrySetting = getCountrySettingForPatient(person);
        return getChannelTypes(countrySetting);
    }

    public static CountrySetting getCountrySettingForPatient(Person person) {
        Map<String, CountrySetting> countrySettingMap = getConfigService()
                .getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY);
        CountrySetting countrySetting = countrySettingMap.get(CFLConstants.DEFAULT_COUNTRY_SETTING_KEY);
        for (Map.Entry<String, CountrySetting> entry : countrySettingMap.entrySet()) {
            if (StringUtils.equalsIgnoreCase(entry.getKey(), PatientUtil.getPatientCountry(person))) {
                countrySetting = entry.getValue();
                break;
            }
        }
        return countrySetting;
    }

    private static String getChannelTypes(CountrySetting countrySetting) {
        String channel = "";
        if (countrySetting.isShouldSendReminderViaSms() && countrySetting.isShouldSendReminderViaCall()) {
            channel = CFLConstants.SMS_CHANNEL_TYPE.concat("," + CFLConstants.CALL_CHANNEL_TYPE);
        } else if (countrySetting.isShouldSendReminderViaCall()) {
            channel = CFLConstants.CALL_CHANNEL_TYPE;
        } else if (countrySetting.isShouldSendReminderViaSms()) {
            channel = CFLConstants.SMS_CHANNEL_TYPE;
        }
        return channel;
    }

    private static ConfigService getConfigService() {
        return Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class);
    }

    private CountrySettingUtil() {
    }
}
