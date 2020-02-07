package org.openmrs.module.cfl.page.controller;

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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.cfl.extension.builder.ExtensionBuilder;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.coreapps.contextmodel.PatientContextModel;
import org.openmrs.module.coreapps.contextmodel.VisitContextModel;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.event.ApplicationEventService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

public class PersonPageController {

    @SuppressWarnings({"checkstyle:parameterNumber", "PMD.ExcessiveParameterList"})
    public Object controller(@RequestParam("patientId") Patient patient,
                             @RequestParam(required = false, value = "app") AppDescriptor app,
                             @RequestParam(required = false, value = "dashboard") String dashboardParam,
                             @InjectBeans PatientDomainWrapper patientDomainWrapper,
                             @SpringBean("adtService") AdtService adtService,
                             @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
                             @SpringBean("applicationEventService") ApplicationEventService applicationEventService,
                             @SpringBean("coreAppsProperties") CoreAppsProperties coreAppsProperties,
                             PageModel model,
                             UiSessionContext sessionContext) {
        try {
            if (!Context.hasPrivilege(CoreAppsConstants.PRIVILEGE_PATIENT_DASHBOARD)) {
                return new Redirect("coreapps", "noAccess", "");
            } else if (patient.isVoided() || patient.isPersonVoided()) {
                return new Redirect("coreapps", "patientdashboard/deletedPatient", "patientId=" + patient.getId());
            }

            String dashboard = StringUtils.isEmpty(dashboardParam) ? "patientDashboard" : dashboardParam;
            patientDomainWrapper.setPatient(patient);
            model.addAttribute("patient", patientDomainWrapper);
            model.addAttribute("app", app);

            Location visitLocation = prepareVisitLocation(adtService, sessionContext);

            VisitDomainWrapper activeVisit = null;
            if (visitLocation != null) {
                activeVisit = adtService.getActiveVisit(patient, visitLocation);
            }
            model.addAttribute("activeVisit", activeVisit);

            AppContextModel contextModel = prepareContextModel(sessionContext, patient, activeVisit);
            model.addAttribute("appContextModel", contextModel);

            ExtensionBuilder builder = new ExtensionBuilder(appFrameworkService, dashboard, contextModel);
            model.addAttribute("overallActions", builder.buildOverallActions());
            model.addAttribute("visitActions", builder.buildVisitActions());
            model.addAttribute("includeFragments", builder.buildInclude());
            model.addAttribute("firstColumnFragments", builder.buildFirstColumn());
            model.addAttribute("secondColumnFragments", builder.buildSecondColumn());
            model.addAttribute("otherActions", builder.buildOtherActions());

            // used for breadcrumbs to link back to the base dashboard in the case when this
            // is used to render a context-specific dashboard
            model.addAttribute("baseDashboardUrl", coreAppsProperties.getDashboardUrl());

            model.addAttribute("dashboard", dashboard);

            applicationEventService.patientViewed(patient, sessionContext.getCurrentUser());

            return null;

        } catch (NullPointerException x) {
            return new Redirect("coreapps", "patientdashboard/patientNotFound", "patientId=" + "Not Found");
        }
    }

    private Location prepareVisitLocation(AdtService adtService, UiSessionContext sessionContext) {
        try {
            return adtService.getLocationThatSupportsVisits(sessionContext.getSessionLocation());
        } catch (IllegalArgumentException ex) {
            // location does not support visits
            return null;
        }
    }

    private AppContextModel prepareContextModel(UiSessionContext sessionContext, Patient patient,
                                                VisitDomainWrapper activeVisit) {
        AppContextModel contextModel = sessionContext.generateAppContextModel();
        contextModel.put("patient", new PatientContextModel(patient));
        contextModel.put("visit", activeVisit == null ? null : new VisitContextModel(activeVisit));

        List<Program> programs = new ArrayList<Program>();
        List<PatientProgram> patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,
                null, null, null, null, null, false);
        for (PatientProgram patientProgram : patientPrograms) {
            programs.add(patientProgram.getProgram());
        }
        contextModel.put("patientPrograms", ConversionUtil.convertToRepresentation(programs,
                Representation.DEFAULT));

        return contextModel;
    }
}
