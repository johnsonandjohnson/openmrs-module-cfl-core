/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.domain.criteria.messages;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.messages.api.dao.PatientAdvancedDao;
import org.openmrs.module.messages.domain.criteria.Condition;
import org.openmrs.module.messages.domain.criteria.QueryCriteria;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ReceivedDosagesConditionTest extends BaseModuleContextSensitiveTest {
    private final Gson gson = new Gson();

    @Autowired
    private PatientAdvancedDao patientAdvancedDao;

    @Before
    public void beforeTest() throws Exception {
        executeDataSet("org/openmrs/include/initialInMemoryTestDataSet.xml");
        executeDataSet("datasets/ReceivedDosagesConditionTest.xml");

        this.authenticate();
        Context
                .getAdministrationService()
                .setGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY, gson.toJson(buildTestVaccinations()));
    }

    @Test
    public void ReceivedDosagesCondition_shouldFilterByEquals() {
        // Given

        // When
        final Condition<?, ?> patientsEqCondition2 = new ReceivedDosagesCondition("=", 2);
        final List<Patient> patientsEq2 =
                patientAdvancedDao.getPatients(0, -1, QueryCriteria.fromConditions(Patient.class, patientsEqCondition2));

        final Condition<?, ?> patientsEqCondition1 = new ReceivedDosagesCondition("=", 1);
        final List<Patient> patientsEq1 =
                patientAdvancedDao.getPatients(0, -1, QueryCriteria.fromConditions(Patient.class, patientsEqCondition1));

        final Condition<?, ?> patientsEqCondition0 = new ReceivedDosagesCondition("=", 0);
        final List<Patient> patientsEq0 =
                patientAdvancedDao.getPatients(0, -1, QueryCriteria.fromConditions(Patient.class, patientsEqCondition0));

        // Then
        assertNotNull(patientsEq2);
        assertThat(patientsEq2.size(), is(1));
        assertThat(patientsEq2.get(0).getId(), is(1001));

        assertNotNull(patientsEq1);
        assertThat(patientsEq1.size(), is(1));
        assertThat(patientsEq1.get(0).getId(), is(1002));

        assertNotNull(patientsEq0);
        assertThat(patientsEq0.size(), is(1));
        assertThat(patientsEq0.get(0).getId(), is(1003));
    }

    @Test
    public void ReceivedDosagesCondition_shouldFilterByLessThen() {
        // Given

        // When
        final Condition<?, ?> patientsLtCondition2 = new ReceivedDosagesCondition("<", 3);
        final List<Patient> patientsLt2 =
                patientAdvancedDao.getPatients(0, -1, QueryCriteria.fromConditions(Patient.class, patientsLtCondition2));

        final Condition<?, ?> patientsLtCondition1 = new ReceivedDosagesCondition("<", 2);
        final List<Patient> patientsLt1 =
                patientAdvancedDao.getPatients(0, -1, QueryCriteria.fromConditions(Patient.class, patientsLtCondition1));

        final Condition<?, ?> patientsLtCondition0 = new ReceivedDosagesCondition("<", 1);
        final List<Patient> patientsLt0 =
                patientAdvancedDao.getPatients(0, -1, QueryCriteria.fromConditions(Patient.class, patientsLtCondition0));

        // Then
        assertNotNull(patientsLt2);
        assertThat(patientsLt2.size(), is(3));
        assertThat(patientsLt2, contains( //
                hasProperty("id", is(1001)), //
                hasProperty("id", is(1002)), //
                hasProperty("id", is(1003))));

        assertNotNull(patientsLt1);
        assertThat(patientsLt1.size(), is(2));
        assertThat(patientsLt1, contains( //
                hasProperty("id", is(1002)), //
                hasProperty("id", is(1003))));

        assertNotNull(patientsLt0);
        assertThat(patientsLt0.size(), is(1));
        assertThat(patientsLt0, contains( //
                hasProperty("id", is(1003))));
    }

    @Test
    public void ReceivedDosagesCondition_shouldFilterByGreaterEqual() {
        // Given

        // When
        final Condition<?, ?> patientsGeCondition2 = new ReceivedDosagesCondition(">=", 2);
        final List<Patient> patientsGe2 =
                patientAdvancedDao.getPatients(0, -1, QueryCriteria.fromConditions(Patient.class, patientsGeCondition2));

        final Condition<?, ?> patientsGeCondition1 = new ReceivedDosagesCondition(">=", 1);
        final List<Patient> patientsGe1 =
                patientAdvancedDao.getPatients(0, -1, QueryCriteria.fromConditions(Patient.class, patientsGeCondition1));

        final Condition<?, ?> patientsGeCondition0 = new ReceivedDosagesCondition(">=", 0);
        final List<Patient> patientsGe0 =
                patientAdvancedDao.getPatients(0, -1, QueryCriteria.fromConditions(Patient.class, patientsGeCondition0));

        // Then
        assertNotNull(patientsGe2);
        assertThat(patientsGe2.size(), is(1));
        assertThat(patientsGe2, contains( //
                hasProperty("id", is(1001))));

        assertNotNull(patientsGe1);
        assertThat(patientsGe1.size(), is(2));
        assertThat(patientsGe1, contains( //
                hasProperty("id", is(1001)), //
                hasProperty("id", is(1002))));

        assertNotNull(patientsGe0);
        assertThat(patientsGe0.size(), is(3));
        assertThat(patientsGe0, contains( //
                hasProperty("id", is(1001)), //
                hasProperty("id", is(1002)), //
                hasProperty("id", is(1003))));
    }

    private Vaccination[] buildTestVaccinations() {
        final List<VisitInformation> visitInformationItems = new ArrayList<VisitInformation>();
        visitInformationItems.add(newVisitInformation("NO_DOSAGE", 0));
        visitInformationItems.add(newVisitInformation("DOSAGE_1", 1));
        visitInformationItems.add(newVisitInformation("DOSAGE_2", 2));
        visitInformationItems.add(newVisitInformation("NO_DOSAGE_END", 2));

        final Vaccination singleVaccination = new Vaccination();
        singleVaccination.setName("ReceivedDosagesConditionTest1");
        singleVaccination.setNumberOfDose(2);
        singleVaccination.setVisits(visitInformationItems);
        return new Vaccination[] {singleVaccination};
    }

    private VisitInformation newVisitInformation(final String visitTypeName, final int doseNumber) {
        final VisitInformation result = new VisitInformation();
        result.setNameOfDose(visitTypeName);
        result.setDoseNumber(doseNumber);
        return result;
    }
}
