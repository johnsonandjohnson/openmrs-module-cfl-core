package org.openmrs.module.cfl.api.contract;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class VaccinationTest {

    private static final String SARS = "SARS.json";
    private static final String COVID = "COVID.json";

    private static final int THREE = 3;

    @Test
    public void shouldFindFutureVisitsForDifferentDoseTypesAndDoseNumberOne() throws IOException {
        Vaccination vaccination = loadVaccinationFromJSON(COVID);
        List<VisitInformation> result = vaccination.findFutureVisits("DOSE 1 VISIT", 1);

        Assert.assertThat(result.size(), is(2));
        assertVisitInformation(result.get(0), "FOLLOW UP", 1);
        assertVisitInformation(result.get(1), "DOSE 1 & 2 VISIT", 2);
    }

    @Test
    public void shouldFindFutureVisitsForDifferentDoseTypesAndDoseNumberTwo() throws IOException {
        Vaccination vaccination = loadVaccinationFromJSON(COVID);
        List<VisitInformation> result = vaccination.findFutureVisits("DOSE 1 & 2 VISIT", 1);

        Assert.assertThat(result.size(), is(2));
        assertVisitInformation(result.get(0), "FOLLOW UP", 2);
        assertVisitInformation(result.get(1), "DOSE 1, 2 & 3 VISIT", THREE);
    }

    @Test
    public void shouldFindFutureVisitsForDifferentDoseTypesAndDoseNumberThree() throws IOException {
        Vaccination vaccination = loadVaccinationFromJSON(COVID);
        List<VisitInformation> result = vaccination.findFutureVisits("DOSE 1, 2 & 3 VISIT", 1);

        Assert.assertThat(result.size(), is(1));
        assertVisitInformation(result.get(0), "FOLLOW UP", THREE);
    }

    @Test
    public void shouldFindFutureVisitsForTheSameDoseTypeAnd1Visits() throws IOException {
        Vaccination vaccination = loadVaccinationFromJSON(SARS);
        List<VisitInformation> result = vaccination.findFutureVisits("Dosing", 1);

        Assert.assertThat(result.size(), is(2));
        assertVisitInformation(result.get(0), "In person follow-up", 1);
        assertVisitInformation(result.get(1), "Dosing", 2);
    }

    @Test
    public void shouldFindFutureVisitsForTheSameDoseTypeAnd2Visits() throws IOException {
        Vaccination vaccination = loadVaccinationFromJSON(SARS);
        List<VisitInformation> result = vaccination.findFutureVisits("Dosing", 2);

        Assert.assertThat(result.size(), is(2));
        assertVisitInformation(result.get(0), "In person follow-up", 2);
        assertVisitInformation(result.get(1), "Dosing", THREE);
    }

    @Test
    public void shouldFindFutureVisitsForTheSameDoseTypeAnd3Visits() throws IOException {
        Vaccination vaccination = loadVaccinationFromJSON(SARS);
        List<VisitInformation> result = vaccination.findFutureVisits("Dosing", THREE);

        Assert.assertThat(result.size(), is(1));
        assertVisitInformation(result.get(0), "In person follow-up", THREE);
    }

    private Vaccination loadVaccinationFromJSON(String jsonFile) throws IOException  {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFile);
        return new Gson().fromJson(IOUtils.toString(in), Vaccination.class);
    }

    private void assertVisitInformation(VisitInformation visitInformation, String visitType, int doseNumber) {
        Assert.assertThat(visitInformation.getNameOfDose(), is(visitType));
        Assert.assertThat(visitInformation.getDoseNumber(), is(doseNumber));
    }
}
