package org.openmrs.module.cfl.api.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.CountrySetting;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.builder.LocationAttributeBuilder;
import org.openmrs.module.cfl.builder.LocationAttributeTypeBuilder;
import org.openmrs.module.cfl.builder.LocationBuilder;
import org.openmrs.module.cfl.builder.PatientIdentifierBuilder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, Daemon.class})
public class CountrySettingUtilTest {

    @Mock
    private PatientService patientService;

    @Mock
    private LocationService locationService;

    @Mock
    private ConfigService configService;

    @Mock
    private AdministrationService administrationService;

    private Patient patient;

    private Map<String, CountrySetting> countrySettingMap;

    private static final String COUNTRY_SETTING = "CountrySetting.json";

    private static final String COUNTRY_DECODED_ATTR_TYPE_NAME = "Country decoded";

    @Before
    public void setUp() throws IOException {
        mockStatic(Context.class);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getRegisteredComponent(anyString(), eq(ConfigService.class))).thenReturn(configService);
        countrySettingMap = createCountrySettingMap();
        when(configService.getCountrySettingMap(CFLConstants.COUNTRY_SETTINGS_MAP_KEY)).thenReturn(countrySettingMap);
        when(locationService.getLocationAttributeTypeByName(COUNTRY_DECODED_ATTR_TYPE_NAME))
                .thenReturn(new LocationAttributeTypeBuilder().build());
    }

    @Test
    public void shouldReturnProperSettingsForArgentinaCountry() {
        patient = createPatient("Argentina");
        when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
        CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(patient);

        Assert.assertThat(countrySetting.getCall(), is("voxeo"));
        Assert.assertThat(countrySetting.getSms(), is("turnIO"));
        Assert.assertFalse(countrySetting.isPerformCallOnPatientRegistration());
        Assert.assertFalse(countrySetting.isSendSmsOnPatientRegistration());
        Assert.assertTrue(countrySetting.isShouldSendReminderViaCall());
        Assert.assertTrue(countrySetting.isShouldSendReminderViaSms());
    }

    @Test
    public void shouldReturnProperSettingsForIndiaCountry() {
        patient = createPatient("India");
        when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
        CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(patient);

        Assert.assertThat(countrySetting.getCall(), is("IMI_MOBILE"));
        Assert.assertThat(countrySetting.getSms(), is("turnIO"));
        Assert.assertTrue(countrySetting.isPerformCallOnPatientRegistration());
        Assert.assertTrue(countrySetting.isSendSmsOnPatientRegistration());
        Assert.assertTrue(countrySetting.isShouldSendReminderViaCall());
        Assert.assertTrue(countrySetting.isShouldSendReminderViaSms());
    }

    @Test
    public void shouldReturnProperSettingsForPolandCountry() {
        patient = createPatient("Poland");
        when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
        CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(patient);

        Assert.assertThat(countrySetting.getCall(), is("voxeo"));
        Assert.assertThat(countrySetting.getSms(), is("turnIO"));
        Assert.assertTrue(countrySetting.isPerformCallOnPatientRegistration());
        Assert.assertFalse(countrySetting.isSendSmsOnPatientRegistration());
        Assert.assertTrue(countrySetting.isShouldSendReminderViaCall());
        Assert.assertFalse(countrySetting.isShouldSendReminderViaSms());
    }

    @Test
    public void shouldReturnDefaultSettingsForCountryThatDoesNotExistInMap() {
        patient = createPatient("China");
        when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
        CountrySetting countrySetting = CountrySettingUtil.getCountrySettingForPatient(patient);

        Assert.assertThat(countrySetting.getCall(), is("nexmo"));
        Assert.assertThat(countrySetting.getSms(), is("turnIO"));
        Assert.assertFalse(countrySetting.isPerformCallOnPatientRegistration());
        Assert.assertTrue(countrySetting.isSendSmsOnPatientRegistration());
        Assert.assertFalse(countrySetting.isShouldSendReminderViaCall());
        Assert.assertTrue(countrySetting.isShouldSendReminderViaSms());
    }

    @Test
    public void shouldReturnSmsAndCallChannelTypeForPatientFromIndia() {
        patient = createPatient("India");
        when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
        String actual = CountrySettingUtil.getChannelTypesForVisitReminder(patient);

        Assert.assertThat(actual, is("SMS,Call"));
    }

    @Test
    public void shouldReturnSmsChannelTypeForPatientFromDefaultCountry() {
        patient = createPatient("Russia");
        when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
        String actual = CountrySettingUtil.getChannelTypesForVisitReminder(patient);

        Assert.assertThat(actual, is("SMS"));
    }

    @Test
    public void shouldReturnCallChannelTypeForPatientFromPhippipines() {
        patient = createPatient("Phippipines");
        when(Context.getPatientService().getPatient(patient.getId())).thenReturn(patient);
        String actual = CountrySettingUtil.getChannelTypesForVisitReminder(patient);

        Assert.assertThat(actual, is("Call"));
    }

    private Patient createPatient(String countryValue) {
        patient = new Patient();
        PatientIdentifier patientIdentifier = new PatientIdentifierBuilder()
                .withId(1).withIdentifier("ABC123").withLocation(new LocationBuilder()
                .withId(1).withName("Test location name").withAttribute(new LocationAttributeBuilder()
                .withId(1).withValue(countryValue).withAttributeType(new LocationAttributeTypeBuilder()
                .withId(1).withName("Country decoded").build()).build()).build()).build();
        Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
        patientIdentifiers.add(patientIdentifier);
        patient.setIdentifiers(patientIdentifiers);
        return patient;
    }

    private Map<String, CountrySetting> createCountrySettingMap() throws IOException {
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

    private String jsonToString(String jsonFile) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFile);
        return IOUtils.toString(in);
    }
}
