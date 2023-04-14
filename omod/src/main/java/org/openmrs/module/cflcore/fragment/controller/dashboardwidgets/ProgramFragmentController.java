/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.fragment.controller.dashboardwidgets;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.service.PatientProgramDetailsService;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ProgramFragmentController {

  private static final String PATIENT_ID_PARAM_NAME = "patientId";
  private static final String PATIENT_PROGRAMS_DETAILS_LIST_ATTRIBUTE_NAME =
      "patientProgramsDetailsList";

  public void controller(
      FragmentModel model,
      FragmentConfiguration configuration,
      @RequestParam(PATIENT_ID_PARAM_NAME) Patient patient) {
    model.addAttribute(
        PATIENT_PROGRAMS_DETAILS_LIST_ATTRIBUTE_NAME,
        Context.getService(PatientProgramDetailsService.class)
            .getPatientProgramsDetails(patient, configuration));
  }
}
