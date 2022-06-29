/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service;

import org.openmrs.Patient;

import java.util.List;

public interface CFLVisitService {

    /**
     * Reschedules (voids and recreates) future visits for list of patients.
     * This method is executed in separate transaction due dividing regimen update job logic into batches
     * to avoid memory issue with one big transaction in case of large number of patients.
     *
     * @param patients list of patients for whom visits are to be rescheduled
     */
    void rescheduleVisitsByPatients(List<Patient> patients);
}
