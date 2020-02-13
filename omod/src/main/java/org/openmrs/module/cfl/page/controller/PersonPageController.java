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
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.extension.builder.PersonExtensionBuilder;
import org.openmrs.module.cfl.extension.domain.PersonDomainWrapper;
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
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

public class PersonPageController {

    private static final PageAction REGULAR_ACTION = null;
    private static final String COREAPPS = "coreapps";
    private static final String PATIENT = "patient";
    private static final String PERSON = "person";
    private static final String PATIENT_ID = "patientId";
    private static final String PERSON_ID = "personId";

    @SuppressWarnings({"checkstyle:parameterNumber", "PMD.ExcessiveParameterList"})
    public PageAction controller(@RequestParam(required = false, value = PATIENT_ID) Person person,
                                 @RequestParam(required = false, value = "app") AppDescriptor app,
                                 @RequestParam(required = false, value = "dashboard") String dashboardParam,
                                 @InjectBeans PatientDomainWrapper patientDomainWrapper,
                                 @InjectBeans PersonDomainWrapper personDomainWrapper,
                                 @SpringBean("adtService") AdtService adtService,
                                 @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
                                 @SpringBean("applicationEventService") ApplicationEventService applicationEventService,
                                 @SpringBean("coreAppsProperties") CoreAppsProperties coreAppsProperties,
                                 @SpringBean("patientService") PatientService patientService,
                                 PageModel model,
                                 UiSessionContext sessionContext) {
        PageAction redirect = getRedirectPage(person);
        if (redirect != REGULAR_ACTION) {
            return redirect;
        }

        RequestParams params = new RequestParams(app, dashboardParam);
        SpringBeans beans = new SpringBeans(adtService, appFrameworkService, applicationEventService,
                coreAppsProperties, patientService);

        if (person.isPatient()) {
            Patient patient = getPatient(beans, person);
            return renderPatientDashboard(patient, params, beans, model, patientDomainWrapper, sessionContext);
        } else {
            return renderCaregiverDashboard(person, params, beans, model, personDomainWrapper, sessionContext);
        }
    }

    private PageAction getRedirectPage(Person person) {
        if (!Context.hasPrivilege(CoreAppsConstants.PRIVILEGE_PATIENT_DASHBOARD)) {
            return new Redirect(COREAPPS, "noAccess", "");
        } else if (person == null) {
            return new Redirect(CFLConstants.MODULE_ID, "personNotFound", PERSON_ID + "=" + "Not Found");
        }
        return REGULAR_ACTION;
    }

    private Patient getPatient(SpringBeans beans, Person person) {
        return beans.patientService.getPatient(((Patient) person).getPatientId());
    }

    private PageAction renderCaregiverDashboard(Person person, RequestParams params, SpringBeans beans, PageModel model,
                                            PersonDomainWrapper personDomainWrapper,
                                            UiSessionContext sessionContext) {
        if (person.isVoided()) {
            return new Redirect(CFLConstants.MODULE_ID, "deletedPerson", PERSON_ID + "=" + person.getId());
        }
        String dashboard = StringUtils.isEmpty(params.dashboardParam) ? "personDashboard" : params.dashboardParam;
        personDomainWrapper.setPerson(person);
        model.addAttribute(PERSON, personDomainWrapper);
        model.addAttribute("app", params.app);
        model.addAttribute("activeVisit", null);

        AppContextModel contextModel = sessionContext.generateAppContextModel();
        model.addAttribute("appContextModel", contextModel);

        PersonExtensionBuilder builder = new PersonExtensionBuilder(beans.appFrameworkService, dashboard, contextModel);
        includeExtensions(model, builder);
        includeDashboardParams(beans, model, dashboard, false);
        return REGULAR_ACTION;
    }

    private PageAction renderPatientDashboard(Patient patient, RequestParams params, SpringBeans beans, PageModel model,
                                          PatientDomainWrapper patientDomainWrapper,
                                          UiSessionContext sessionContext) {
        if (patient.isVoided() || patient.isPersonVoided()) {
            return new Redirect(COREAPPS, "patientdashboard/deletedPatient",
                    PATIENT_ID + "=" + patient.getId());
        }
        String dashboard = StringUtils.isEmpty(params.dashboardParam) ? "patientDashboard" : params.dashboardParam;
        patientDomainWrapper.setPatient(patient);
        model.addAttribute(PATIENT, patientDomainWrapper);
        model.addAttribute("app", params.app);

        Location visitLocation = prepareVisitLocation(beans.adtService, sessionContext);
        VisitDomainWrapper activeVisit = prepareActiveVisit(patient, beans, visitLocation);
        model.addAttribute("activeVisit", activeVisit);

        AppContextModel contextModel = prepareContextModel(sessionContext, patient, activeVisit);
        model.addAttribute("appContextModel", contextModel);

        PersonExtensionBuilder builder = new PersonExtensionBuilder(beans.appFrameworkService, dashboard, contextModel);
        includeExtensions(model, builder);
        includeDashboardParams(beans, model, dashboard, true);

        beans.applicationEventService.patientViewed(patient, sessionContext.getCurrentUser());
        return REGULAR_ACTION;
    }

    private void includeDashboardParams(SpringBeans beans, PageModel model, String dashboard, boolean isPatient) {
        // used for breadcrumbs to link back to the base dashboard in the case when this
        // is used to render a context-specific dashboard
        model.addAttribute("baseDashboardUrl", beans.coreAppsProperties.getDashboardUrl());
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("isPatient", isPatient);
    }

    private void includeExtensions(PageModel model, PersonExtensionBuilder builder) {
        model.addAttribute(PersonExtensionBuilder.OVERALL_ACTIONS, builder.buildOverallActions());
        model.addAttribute(PersonExtensionBuilder.VISIT_ACTIONS, builder.buildVisitActions());
        model.addAttribute(PersonExtensionBuilder.INCLUDE_FRAGMENTS, builder.buildIncludeFragments());
        model.addAttribute(PersonExtensionBuilder.FIRST_COLUMN, builder.buildFirstColumn());
        model.addAttribute(PersonExtensionBuilder.SECOND_COLUMN, builder.buildSecondColumn());
        model.addAttribute(PersonExtensionBuilder.OTHER_ACTIONS, builder.buildOtherActions());
    }

    private VisitDomainWrapper prepareActiveVisit(Patient patient, SpringBeans beans, Location visitLocation) {
        VisitDomainWrapper activeVisit = null;
        if (visitLocation != null) {
            activeVisit = beans.adtService.getActiveVisit(patient, visitLocation);
        }
        return activeVisit;
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
        contextModel.put(PATIENT, new PatientContextModel(patient));
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

    private class RequestParams {
        private AppDescriptor app;
        private String dashboardParam;

        RequestParams(AppDescriptor app, String dashboardParam) {
            this.app = app;
            this.dashboardParam = dashboardParam;
        }
    }

    private class SpringBeans {
        private AdtService adtService;
        private AppFrameworkService appFrameworkService;
        private ApplicationEventService applicationEventService;
        private CoreAppsProperties coreAppsProperties;
        private PatientService patientService;

        SpringBeans(AdtService adtService, AppFrameworkService appFrameworkService,
                           ApplicationEventService applicationEventService, CoreAppsProperties coreAppsProperties,
                           PatientService patientService) {
            this.adtService = adtService;
            this.appFrameworkService = appFrameworkService;
            this.applicationEventService = applicationEventService;
            this.coreAppsProperties = coreAppsProperties;
            this.patientService = patientService;
        }
    }
}
