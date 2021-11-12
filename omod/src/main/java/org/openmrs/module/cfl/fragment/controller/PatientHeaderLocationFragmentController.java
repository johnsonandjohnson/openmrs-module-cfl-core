package org.openmrs.module.cfl.fragment.controller;

import org.openmrs.Patient;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

public class PatientHeaderLocationFragmentController extends HeaderLocationFragment {

  private static final String PATIENT_LOCATION_ATTRIBUTE_NAME = "patientLocation";

  public void controller(
      FragmentModel model, @RequestParam(value = "patientId", required = false) Patient patient) {
    model.addAttribute(PATIENT_LOCATION_ATTRIBUTE_NAME, getLocationName(patient));
  }
}
