package org.openmrs.module.cfl.builder;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.openmrs.module.cfl.api.contract.countrysettings.AllCountrySettings;
import org.openmrs.module.cfl.api.contract.countrysettings.CountrySettings;

public final class CountrySettingBuilder {

    private static final String COUNTRY_SETTING = "CountrySetting.json";

    public static AllCountrySettings createAllCountrySettings() throws IOException {
        String countrySettings = jsonToString(COUNTRY_SETTING);
        return new AllCountrySettings(new Gson().fromJson(countrySettings, CountrySettings[].class));
    }

    private static String jsonToString(String jsonFile) throws IOException {
        InputStream in = CountrySettingBuilder.class.getClassLoader().getResourceAsStream(jsonFile);
        return IOUtils.toString(in);
    }

    private CountrySettingBuilder() {
    }
}
