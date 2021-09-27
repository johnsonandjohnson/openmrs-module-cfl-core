package org.openmrs.module.cfl.api.util;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Randomization;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.cfl.api.helper.VisitHelper;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, Daemon.class})
public class VisitUtilTest {

    private static final String EXAMPLE_VACC_PROGRAM_NAME = "Vac_3 (three doses)";

    private static final String COVID = "COVID.json";

    private static final String RANDOMIZATION = "Randomization.json";

    private static final String OCCURRED = "OCCURRED";

    private Patient patient;

    @Mock
    private VisitService visitService;

    @Mock
    private ConfigService configService;

    @Mock
    private AdministrationService administrationService;

    @Before
    public void setUp() {
        mockStatic(Context.class);
        patient = createPatient();

        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getRegisteredComponent(anyString(), eq(ConfigService.class)))
                .thenReturn(configService);
        when(Context.getAdministrationService()).thenReturn(administrationService);

        when(visitService.getAllVisitAttributeTypes()).thenReturn(VisitHelper.createVisitAttrTypes());
        when(configService.getVaccinationProgram(patient)).thenReturn(EXAMPLE_VACC_PROGRAM_NAME);
        when(administrationService.getGlobalProperty(CFLConstants.STATUS_OF_OCCURRED_VISIT_KEY)).thenReturn(OCCURRED);
    }

    @Test
    public void shouldFindLastDosingVisit() throws IOException {
        List<Visit> visits = new ArrayList<>();
        visits.add(VisitHelper.createVisit(1, patient, "DOSE 1 VISIT", "OCCURRED"));
        visits.add(VisitHelper.createVisit(2, patient, "FOLLOW UP", "OCCURRED"));
        visits.add(VisitHelper.createVisit(3, patient, "DOSE 1 & 2 VISIT", "OCCURRED"));
        when(visitService.getVisitsByPatient(patient)).thenReturn(visits);
        Vaccination vaccination = loadVaccinationFromJSON(COVID);

        Visit result = VisitUtil.getLastDosingVisit(patient, vaccination);

        Assert.assertThat(result.getId(), is(3));
        Assert.assertThat(result.getVisitType().getName(), is("DOSE 1 & 2 VISIT"));
        Assert.assertThat(VisitUtil.getVisitStatus(result), is("OCCURRED"));
    }

    @Test
    public void shouldReturnProperVisitStatus() {
        List<Visit> visits = new ArrayList<>();
        visits.add(VisitHelper.createVisit(1, patient, "DOSE 1 VISIT", "OCCURRED"));
        visits.add(VisitHelper.createVisit(2, patient, "FOLLOW UP", "OCCURRED"));
        visits.add(VisitHelper.createVisit(3, patient, "DOSE 1 & 2 VISIT", "SCHEDULED"));
        when(visitService.getVisitsByPatient(patient)).thenReturn(visits);

        String result = VisitUtil.getVisitStatus(visits.get(0));
        Assert.assertThat(result, is("OCCURRED"));

        String result2 = VisitUtil.getVisitStatus(visits.get(2));
        Assert.assertThat(result2, is("SCHEDULED"));
    }

    @Test
    public void shouldReturnProperNumberOfDosesForPatient() throws IOException {
        Vaccination[] vaccinations = new Vaccination[] {loadVaccinationFromJSON(COVID)};
        when(configService.getRandomizationGlobalProperty()).thenReturn(new Randomization(vaccinations));
        when(configService.getVaccinationProgram(patient)).thenReturn(EXAMPLE_VACC_PROGRAM_NAME);

        int actual = VisitUtil.getNumberOfDosesForPatient(patient);

        assertEquals(actual, 3);
    }

    @Test
    public void shouldReturnInformationIfGivenVisitIsLastDosingVisit() throws IOException {
        Vaccination[] vaccinations = loadVaccinationsFromJSON(RANDOMIZATION);
        Randomization randomization = new Randomization(vaccinations);
        when(configService.getRandomizationGlobalProperty()).thenReturn(randomization);
        when(configService.getVaccinationProgram(patient)).thenReturn("Vaccination_1");

        Vaccination vaccination = vaccinations[0];
        List<VisitInformation> visits = vaccination.getVisits();

        assertFalse(VisitUtil.isLastPatientDosingVisit(patient, visits.get(0)));
        assertFalse(VisitUtil.isLastPatientDosingVisit(patient, visits.get(1)));
        assertFalse(VisitUtil.isLastPatientDosingVisit(patient, visits.get(2)));
        assertFalse(VisitUtil.isLastPatientDosingVisit(patient, visits.get(3)));
        assertFalse(VisitUtil.isLastPatientDosingVisit(patient, visits.get(4)));
        assertTrue(VisitUtil.isLastPatientDosingVisit(patient, visits.get(5)));
        assertFalse(VisitUtil.isLastPatientDosingVisit(patient, visits.get(6)));
        assertFalse(VisitUtil.isLastPatientDosingVisit(patient, visits.get(7)));
    }

    private Patient createPatient() {
        patient = new Patient();
        patient.setId(1);
        return patient;
    }

    private Vaccination loadVaccinationFromJSON(String jsonFile) throws IOException  {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFile);
        return new Gson().fromJson(IOUtils.toString(in), Vaccination.class);
    }

    private Vaccination[] loadVaccinationsFromJSON(String jsonFile) throws IOException  {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFile);
        return new Gson().fromJson(IOUtils.toString(in), Vaccination[].class);
    }
}
