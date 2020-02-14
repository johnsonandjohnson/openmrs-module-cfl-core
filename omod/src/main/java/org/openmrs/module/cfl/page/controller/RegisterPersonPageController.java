/*
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

package org.openmrs.module.cfl.page.controller;

import org.codehaus.jackson.JsonNode;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.cfl.form.RegisterPersonFormBuilder;
import org.openmrs.module.registrationapp.AddressSupportCompatibility;
import org.openmrs.module.registrationapp.NameSupportCompatibility;
import org.openmrs.module.registrationapp.model.NavigableFormStructure;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Based on openmrs-module-registrationapp v1.13.0
 * omod/src/main/java/org/openmrs/module/registrationapp/page/controller/RegisterPatientPageController.java
 */
public class RegisterPersonPageController {

    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String DOT = ".";
    private static final String MALE_SIGN = "M";
    private static final String FEMALE_SIGN = "F";

    private static final String APP_ID_PROP = "appId";
    private static final String PERSON_PROP = "person";
    private static final String FRAMEWORK_SERVICE_PROP = "appFrameworkService";
    private static final String BREADCRUMB_OVERRIDE_PROP = "breadcrumbOverride";
    private static final String FORM_STRUCTURE_PROP = "formStructure";
    private static final String NAME_TEMPLATE_PROP = "nameTemplate";
    private static final String ADDRESS_TEMPLATE_PROP = "addressTemplate";
    private static final String ENABLE_OVERRIDE_PROP = "enableOverrideOfAddressPortlet";
    private static final String RELATIONSHIP_TYPES_PROP = "relationshipTypes";
    private static final String GENDER_OPTIONS_PROP = "genderOptions";
    private static final String INCLUDE_FRAGMENTS_PROP = "includeFragments";
    private static final String REGISTER_PERSON_PROP = "registerPerson";
    private static final String ADDRESS_HIERARCHY_PROP = "addresshierarchy";
    private static final String DASHBOARD_LINK_PROP = "personDashboardLink";
    private static final String COMBINE_SUB_SECTIONS_PROP = "combineSubSections";
    private static final String TITLE_PROP = "mainTitle";
    private static final String ENABLE_OVERRIDE_PATH = ADDRESS_HIERARCHY_PROP + DOT + ENABLE_OVERRIDE_PROP;
    private static final String INCLUDE_FRAGMENTS_PATH = REGISTER_PERSON_PROP + DOT + INCLUDE_FRAGMENTS_PROP;

    public void get(UiSessionContext sessionContext,
                    PageModel model,
                    @RequestParam(APP_ID_PROP) AppDescriptor app,
                    @RequestParam(value = BREADCRUMB_OVERRIDE_PROP, required = false) String breadcrumbOverride,
                    @ModelAttribute(PERSON_PROP) @BindParams Person person,
                    @SpringBean(FRAMEWORK_SERVICE_PROP) AppFrameworkService appFrameworkService) {
        sessionContext.requireAuthentication();
        addModelAttributes(model, person, app, breadcrumbOverride, appFrameworkService);
    }

    @SuppressWarnings({"checkstyle:ParameterAssignment", "PMD.AvoidReassigningParameters"})
    public void addModelAttributes(PageModel model,
                                   Person person,
                                   AppDescriptor app,
                                   String breadcrumbOverride,
                                   AppFrameworkService appFrameworkService) {
        if (person == null) {
            person = new Person();
        }

        NavigableFormStructure formStructure = RegisterPersonFormBuilder.buildFormStructure(app);
        NameSupportCompatibility nameSupport = Context
                .getRegisteredComponent(NameSupportCompatibility.ID, NameSupportCompatibility.class);
        AddressSupportCompatibility addressSupport = Context
                .getRegisteredComponent(AddressSupportCompatibility.ID, AddressSupportCompatibility.class);

        model.addAttribute(PERSON_PROP, person);
        model.addAttribute(APP_ID_PROP, app.getId());
        model.addAttribute(TITLE_PROP,  app.getLabel());
        model.addAttribute(FORM_STRUCTURE_PROP, formStructure);
        model.addAttribute(NAME_TEMPLATE_PROP, nameSupport.getDefaultLayoutTemplate());
        model.addAttribute(ADDRESS_TEMPLATE_PROP, addressSupport.getAddressTemplate());
        model.addAttribute(DASHBOARD_LINK_PROP, getDashboardLinkProp(app));
        model.addAttribute(COMBINE_SUB_SECTIONS_PROP, getSubSectionProp(app));
        model.addAttribute(ENABLE_OVERRIDE_PROP, Context.getAdministrationService().getGlobalProperty(
                ENABLE_OVERRIDE_PATH, Boolean.FALSE.toString()));
        model.addAttribute(BREADCRUMB_OVERRIDE_PROP, breadcrumbOverride);
        model.addAttribute(RELATIONSHIP_TYPES_PROP, Context.getPersonService().getAllRelationshipTypes());

        List<Extension> includeFragments = appFrameworkService.getExtensionsForCurrentUser(INCLUDE_FRAGMENTS_PATH);
        Collections.sort(includeFragments);
        model.addAttribute(INCLUDE_FRAGMENTS_PROP, includeFragments);
        model.addAttribute(GENDER_OPTIONS_PROP, getGenderOptions(app));
    }

    private static String[] getGenderOptions(AppDescriptor app) {
        if (app.getConfig().get(GENDER_OPTIONS_PROP) != null) {
            return app.getConfig().get(GENDER_OPTIONS_PROP).getTextValue().replace(SPACE, EMPTY).split(COMMA);
        }
        return new String[]{MALE_SIGN, FEMALE_SIGN};
    }

    private boolean getSubSectionProp(AppDescriptor app) {
        JsonNode jsonProp = app.getConfig().get(COMBINE_SUB_SECTIONS_PROP);
        return jsonProp != null && jsonProp.getBooleanValue();
    }

    private String getDashboardLinkProp(AppDescriptor app) {
        JsonNode jsonProp = app.getConfig().get(DASHBOARD_LINK_PROP);
        return (jsonProp != null) ? jsonProp.getTextValue() : null;
    }
}
