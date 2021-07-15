package org.openmrs.module.cfl.builder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.commons.io.IOUtils;
import org.openmrs.module.cfl.api.contract.CountrySetting;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class CountrySettingBuilder {

    public static final String COLUMBIA = "Columbia";
    public static final String COLUMBIA_SMS_CONFIG = "columbiaTurnIO";

    public static final String PHIPPIPINES = "Phippipines";
    public static final String PHIPPIPINES_CALL_CONFIG = "voxeo";

    private static final String COUNTRY_SETTING = "CountrySetting.json";

    public static Map<String, CountrySetting> createCountrySettingMap() throws IOException {
        Gson gson = new Gson();
        String countrySettings = jsonToString(COUNTRY_SETTING);
        JsonArray jsonArray = gson.fromJson(countrySettings, JsonArray.class);
        Map<String, CountrySetting> countrySettingMap = new HashMap<String, CountrySetting>();
        for (JsonElement jsonElement : jsonArray) {
            for (Map.Entry<String, JsonElement> en : jsonElement.getAsJsonObject().entrySet()) {
                countrySettingMap.put(en.getKey(), gson.fromJson(en.getValue().toString(), CountrySetting.class));
            }
        }
        return countrySettingMap;
    }

    private static String jsonToString(String jsonFile) throws IOException {
        InputStream in = CountrySettingBuilder.class.getClassLoader().getResourceAsStream(jsonFile);
        return IOUtils.toString(in);
    }

    private CountrySettingBuilder() {
    }
}
