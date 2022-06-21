/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.page.controller;

import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import static org.openmrs.module.cfl.CFLRegisterPersonConstants.APP_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.BREADCRUMB_OVERRIDE_PROP;

/**
 * It is based of org.openmrs.module.coreapps.page.controller.findpatient.FindPatientPageController
 */
public class FindPersonPageController {
  private static final String REGISTRATION_APP_LINK_ATTR = "registrationAppLink";

  /**
   * This page is built to be shared across multiple apps. To use it, you must pass an APP_PROP
   * request parameter, which must be the id of an existing app that is an instance of
   * coreapps.template.findPatient
   *
   * @param model model
   * @param app app
   * @param breadcrumbOverride breadcrumbOverride
   */
  public void get(
      PageModel model,
      @RequestParam(APP_PROP) AppDescriptor app,
      @RequestParam(value = BREADCRUMB_OVERRIDE_PROP, required = false) String breadcrumbOverride) {

    model.addAttribute(BREADCRUMB_OVERRIDE_PROP, breadcrumbOverride);
    model.addAttribute("afterSelectedUrl", app.getConfig().get("afterSelectedUrl").getTextValue());
    model.addAttribute("heading", app.getConfig().get("heading").getTextValue());
    model.addAttribute("label", app.getConfig().get("label").getTextValue());
    model.addAttribute(
        "showLastViewedPatients", app.getConfig().get("showLastViewedPatients").getBooleanValue());
    if (app.getConfig().get(REGISTRATION_APP_LINK_ATTR) == null) {
      model.addAttribute(REGISTRATION_APP_LINK_ATTR, "");
    } else {
      model.addAttribute(
          REGISTRATION_APP_LINK_ATTR,
          app.getConfig().get(REGISTRATION_APP_LINK_ATTR).getTextValue());
    }
  }
}
