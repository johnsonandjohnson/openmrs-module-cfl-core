/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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

package org.openmrs.module.cflcore.page.controller;

import org.codehaus.jackson.JsonNode;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.cflcore.form.RegisterPersonFormBuilder;
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
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.ADDRESS_TEMPLATE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.APP_ID_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.BREADCRUMB_OVERRIDE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.COMBINE_SUB_SECTIONS_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.COMMA;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.DASHBOARD_LINK_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.ENABLE_OVERRIDE_PATH;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.ENABLE_OVERRIDE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.FEMALE_SIGN;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.FORM_STRUCTURE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.FRAMEWORK_SERVICE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.GENDER_OPTIONS_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.INCLUDE_FRAGMENTS_PATH;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.INCLUDE_FRAGMENTS_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.MALE_SIGN;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.NAME_TEMPLATE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.PERSON_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.RELATIONSHIP_TYPES_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.SPACE;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.TITLE_PROP;

/**
 * Based on openmrs-module-registrationapp v1.13.0
 * omod/src/main/java/org/openmrs/module/registrationapp/page/controller/RegisterPatientPageController.java
 */
public class RegisterPersonPageController {

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
