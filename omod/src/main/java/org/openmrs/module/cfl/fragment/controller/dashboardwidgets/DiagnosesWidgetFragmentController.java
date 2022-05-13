/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.fragment.controller.dashboardwidgets;

/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

import org.openmrs.Patient;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiagnosesWidgetFragmentController {

    private static final String PATIENT = "patient";
    private static final String RECENT_DIAGNOSES_ATTR_NAME = "recentDiagnoses";
    private static final String PRIMARY_DIAGNOSIS_ATTR_NAME = "primaryDiagnosis";
    private static final String SECONDARY_DIAGNOSIS_ATTR_NAME = "secondaryDiagnosis";
    private static final String RECENT_PERIOD_IN_DAYS_ATTR_NAME = "recentPeriodInDays";
    private static final String PRIMARY = "PRIMARY";

    @SuppressWarnings({"checkstyle:ParameterAssignment", "PMD.AvoidReassigningParameters"})
    public void controller(FragmentConfiguration config, @InjectBeans PatientDomainWrapper patientWrapper,
                           @InjectBeans CoreAppsProperties properties) {
        config.require(PATIENT);
        Object patient = config.get(PATIENT);

        if (patient instanceof Patient) {
            patientWrapper.setPatient((Patient) patient);
            config.addAttribute(PATIENT, patientWrapper);
        } else if (patient instanceof PatientDomainWrapper) {
            patientWrapper = (PatientDomainWrapper) patient;
        } else {
            throw new IllegalArgumentException("Patient must be of type Patient or PatientDomainWrapper");
        }

        int days = properties.getRecentDiagnosisPeriodInDays();
        Calendar recent = Calendar.getInstance();
        recent.set(Calendar.DATE, -days);

        List<Diagnosis> recentDiagnoses = patientWrapper.getUniqueDiagnosesSince(recent.getTime());
        List<Diagnosis> primaryDiagnosis = new ArrayList<>();
        List<Diagnosis> secondaryDiagnosis = new ArrayList<>();

        for (Diagnosis diagnosis : recentDiagnoses) {
            if (diagnosis.getOrder().name().equalsIgnoreCase(PRIMARY)) {
                primaryDiagnosis.add(diagnosis);
            } else {
                secondaryDiagnosis.add(diagnosis);
            }
        }
        config.addAttribute(RECENT_DIAGNOSES_ATTR_NAME, recentDiagnoses);
        config.addAttribute(PRIMARY_DIAGNOSIS_ATTR_NAME, primaryDiagnosis);
        config.addAttribute(SECONDARY_DIAGNOSIS_ATTR_NAME, secondaryDiagnosis);
        config.addAttribute(RECENT_PERIOD_IN_DAYS_ATTR_NAME, days);
    }
}
