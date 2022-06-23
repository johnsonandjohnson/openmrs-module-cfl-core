/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.service.impl;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cflcore.api.service.IrisPatientService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class IrisPatientServiceImpl extends BaseOpenmrsService implements IrisPatientService {

    private PatientService patientService;

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Patient savePatient(Patient patient) {
       return patientService.savePatient(patient);
    }
}
