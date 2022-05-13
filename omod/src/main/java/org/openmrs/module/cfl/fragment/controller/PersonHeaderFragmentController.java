/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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

package org.openmrs.module.cfl.fragment.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.extension.domain.PersonDomainWrapper;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.coreapps.contextmodel.PersonContextModel;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Collections;
import java.util.List;

/**
 * Ideally you pass in a PersonDomainWrapper as the "person" config parameter. But if you pass in
 * a Person, then this controller will wrap that for you.
 */
public class PersonHeaderFragmentController extends HeaderFragment {

    private static final String FIRST_LINE = "firstLineFragments";
    private static final String SECOND_LINE = "secondLineFragments";
    private static final String PERSON = "person";
    private static final String PERSON_NAMES = "personNames";
    private static final String TELEPHONE = "telephone";
    private static final String PERSON_IDENTIFIER_LABEL = "personIdentifierLabel";
    private static final String PERSON_IDENTIFIER = "personIdentifier";
    private static final String PERSON_HEADER = "personHeader";
    private static final String APP_CONTEXT_MODEL = "appContextModel";

    @SuppressWarnings({"checkstyle:ParameterNumber", "checkstyle:ParameterAssignment",
            "PMD.ExcessiveParameterList", "PMD.AvoidReassigningParameters"})
    public void controller(FragmentConfiguration config,
                           @SpringBean("coreAppsProperties") CoreAppsProperties coreAppsProperties,
                           @FragmentParam(required = false, value = APP_CONTEXT_MODEL) AppContextModel appContextModel,
                           @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
                           @FragmentParam(PERSON) Object person, @InjectBeans PersonDomainWrapper wrapper,
                           UiSessionContext sessionContext,
                           FragmentModel model) {
        if (person instanceof Person) {
            wrapper.setPerson((Person) person);
        } else {
            wrapper = (PersonDomainWrapper) person;
        }
        config.addAttribute(PERSON, wrapper);
        config.addAttribute(PERSON_NAMES, getNames(wrapper.getPersonName()));
        config.addAttribute(TELEPHONE, wrapper.getPerson().getAttribute(CFLConstants.TELEPHONE_ATTRIBUTE_NAME));
        model.addAttribute(PERSON_IDENTIFIER_LABEL, getIdentifierLabelSetting());
        model.addAttribute(PERSON_IDENTIFIER, getIdentifierValue(wrapper));

        if (appContextModel == null) {
            AppContextModel contextModel = sessionContext.generateAppContextModel();
            contextModel.put(PERSON, new PersonContextModel(wrapper.getPerson()));
            model.addAttribute(APP_CONTEXT_MODEL, contextModel);
        }

        List<Extension> firstLineFragments = appFrameworkService
                .getExtensionsForCurrentUser(PERSON_HEADER + "." + FIRST_LINE);
        Collections.sort(firstLineFragments);
        model.addAttribute(FIRST_LINE, firstLineFragments);

        List<Extension> secondLineFragments = appFrameworkService
                .getExtensionsForCurrentUser(PERSON_HEADER + "." + SECOND_LINE);
        Collections.sort(secondLineFragments);
        model.addAttribute(SECOND_LINE, secondLineFragments);

        config.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
    }

    /**
     * Returns the value of global property for identifier label on person header
     * @return - person header identifier label setting
     */
    private String getIdentifierLabelSetting() {
        return Context.getAdministrationService().getGlobalProperty(CFLConstants.PERSON_HEADER_IDENTIFIER_LABEL_KEY);
    }

    /**
     * Returns the value of person identifier attribute based on global settings configuration
     * @return - the person identifier attribute value
     */
    private PersonAttribute getIdentifierValue(
            @InjectBeans PersonDomainWrapper wrapper) {
        PersonAttributeType type = getPersonIdentifierAttributeType();
        return wrapper.getPerson().getAttribute(type);
    }

    /**
     * Returns the person attribute type which should be used to store the person identifier.
     * The global setting is used to determine the attribute type.
     *
     * @return - attribute type or null if global setting incorrect
     */
    private PersonAttributeType getPersonIdentifierAttributeType() {
        PersonAttributeType type = null;
        String identifierAttributeTypeUUID = getPersonIdentifierTypeSetting();
        if (StringUtils.isNotBlank(identifierAttributeTypeUUID)) {
            type = Context.getPersonService().getPersonAttributeTypeByUuid(identifierAttributeTypeUUID);
        }
        return type;
    }

    /**
     * Returns the settings for person identifier attribute type.
     *
     * @return - setting value
     */
    private String getPersonIdentifierTypeSetting() throws APIException  {
        return Context.getAdministrationService().getGlobalProperty(CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_KEY);
    }
}
