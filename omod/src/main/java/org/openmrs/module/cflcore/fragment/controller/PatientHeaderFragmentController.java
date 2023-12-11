/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.fragment.controller;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.api.dto.PatientHeaderConfigDTO;
import org.openmrs.module.cflcore.api.dto.PatientHeaderFieldDTO;
import org.openmrs.module.cflcore.api.patienheader.model.PatientHeaderConfigModel;
import org.openmrs.module.cflcore.api.patienheader.model.PatientHeaderFieldModel;
import org.openmrs.module.cflcore.api.patienheader.PatientHeaderValueAccessor;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Ideally you pass in a PatientDomainWrapper as the "patient" config parameter. But if you pass in
 * a Patient, then this controller will wrap that for you.
 */
public class PatientHeaderFragmentController extends HeaderFragment {

  public void controller(
      FragmentConfiguration config,
      @FragmentParam("patient") Patient patient,
      FragmentModel model,
      PageContext pageContext) {
    config.addAttribute("patient", patient);

    AppDescriptor headerApp =
        Context.getService(AppFrameworkService.class)
            .getApp(CFLConstants.CONFIGURABLE_PATIENT_HEADER_APP_NAME);
    PatientHeaderConfigModel patientHeaderConfigModel =
        new ObjectMapper().convertValue(headerApp.getConfig(), PatientHeaderConfigModel.class);

    model.addAttribute(
        "patientHeaderConfigDTO", buildPatientHeaderConfigDTO(patientHeaderConfigModel, patient));
    model.addAttribute(
        "isExtraTitleFieldsAvailable",
        patientHeaderConfigModel.getTitleFields().stream()
            .anyMatch(field -> !field.isMainTitleField()));
    model.addAttribute("isPatientDashboard", isPatientDashboard(pageContext));
  }

  private PatientHeaderConfigDTO buildPatientHeaderConfigDTO(
      PatientHeaderConfigModel patientHeaderConfigModel, Patient patient) {
    List<PatientHeaderFieldDTO> titleFieldDTOList =
        buildPatientHeaderFieldDTOList(patientHeaderConfigModel.getTitleFields(), patient);
    List<PatientHeaderFieldDTO> attributeFieldDTOList =
        buildPatientHeaderFieldDTOList(patientHeaderConfigModel.getAttributeFields(), patient);

    return new PatientHeaderConfigDTO(
        titleFieldDTOList,
        attributeFieldDTOList,
        patientHeaderConfigModel.isDeleteButtonOnPatientDashboardVisible(),
        patientHeaderConfigModel.isUpdateStatusButtonVisible());
  }

  private List<PatientHeaderFieldDTO> buildPatientHeaderFieldDTOList(
      List<PatientHeaderFieldModel> fieldModelList, Patient patient) {
    List<PatientHeaderFieldDTO> resultList = new ArrayList<>();

    PatientHeaderValueAccessor patientHeaderValueAccessor = new PatientHeaderValueAccessor(patient);
    for (PatientHeaderFieldModel fieldModel : fieldModelList) {
      resultList.add(
          new PatientHeaderFieldDTO(
              fieldModel.getLabel(),
              patientHeaderValueAccessor.getValue(fieldModel),
              fieldModel.getType(),
              fieldModel.getFormat(),
              fieldModel.isMainTitleField()));
    }

    return resultList;
  }

  private boolean isPatientDashboard(PageContext pageContext) {
    String dashboardType = (String) pageContext.getModel().getAttribute("dashboard");
    return StringUtils.equalsIgnoreCase(dashboardType, "patientDashboard");
  }
}
