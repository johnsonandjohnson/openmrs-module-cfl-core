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
import org.openmrs.module.cfl.api.helper.VisitHelper;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, Daemon.class})
public class VisitUtilTest {

    @Mock
    private VisitService visitService;

    @Mock
    private ConfigService configService;

    @Mock
    private AdministrationService administrationService;

    private List<Visit> visits;

    private Patient patient;

    private Randomization randomization;

    private static final String EXAMPLE_VACC_PROGRAM_NAME = "Vac_3 (three doses)";

    private static final String RANDOMIZATION = "Randomization.json";

    private static final String OCCURRED = "OCCURRED";

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
        visits = new ArrayList<Visit>();
        visits.add(VisitHelper.createVisit(1, patient, "DOSE 1 VISIT", "OCCURRED"));
        visits.add(VisitHelper.createVisit(2, patient, "FOLLOW UP", "OCCURRED"));
        visits.add(VisitHelper.createVisit(3, patient, "DOSE 1 & 2 VISIT", "OCCURRED"));
        when(visitService.getVisitsByPatient(patient)).thenReturn(visits);
        randomization = loadRandomizationFromJSON(RANDOMIZATION);
        when(configService.getRandomizationGlobalProperty()).thenReturn(randomization);

        Visit result = VisitUtil.getLastDosingVisit(patient);

        Assert.assertThat(result.getId(), is(3));
        Assert.assertThat(result.getVisitType().getName(), is("DOSE 1 & 2 VISIT"));
        Assert.assertThat(VisitUtil.getVisitStatus(result), is("OCCURRED"));
    }

    @Test
    public void shouldReturnProperVisitStatus() {
        visits = new ArrayList<Visit>();
        visits.add(VisitHelper.createVisit(1, patient, "DOSE 1 VISIT", "OCCURRED"));
        visits.add(VisitHelper.createVisit(2, patient, "FOLLOW UP", "OCCURRED"));
        visits.add(VisitHelper.createVisit(3, patient, "DOSE 1 & 2 VISIT", "SCHEDULED"));
        when(visitService.getVisitsByPatient(patient)).thenReturn(visits);

        String result = VisitUtil.getVisitStatus(visits.get(0));
        Assert.assertThat(result, is("OCCURRED"));

        String result2 = VisitUtil.getVisitStatus(visits.get(2));
        Assert.assertThat(result2, is("SCHEDULED"));
    }

    private Patient createPatient() {
        patient = new Patient();
        patient.setId(1);
        return patient;
    }

    private Randomization loadRandomizationFromJSON(String jsonFile) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFile);
        return new Randomization(new Gson().fromJson(IOUtils.toString(in), Vaccination[].class));
    }
}
