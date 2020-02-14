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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.extension.domain.PersonDomainWrapper;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.coreapps.NameSupportCompatibility;
import org.openmrs.module.coreapps.contextmodel.PersonContextModel;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ideally you pass in a PersonDomainWrapper as the "person" config parameter. But if you pass in
 * a Person, then this controller will wrap that for you.
 */
public class PersonHeaderFragmentController {

    private static final String FIRST_LINE = "firstLineFragments";
    private static final String SECOND_LINE = "secondLineFragments";
    private static final String PERSON = "person";
    private static final String PERSON_NAMES = "personNames";
    private static final String TELEPHONE = "telephone";
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

    private Map<String, String> getNames(PersonName personName) {

        NameSupportCompatibility nameSupport = Context
                .getRegisteredComponent("coreapps.NameSupportCompatibility", NameSupportCompatibility.class);

        Map<String, String> nameFields = new LinkedHashMap<String, String>();
        List<List<Map<String, String>>> lines = nameSupport.getLines();
        String layoutToken = nameSupport.getLayoutToken();

        // note that the assumption is one one field per "line",
        // otherwise the labels that appear under each field may not render properly
        try {
            for (List<Map<String, String>> line : lines) {
                String nameLabel = "";
                StringBuilder nameLine = new StringBuilder();
                boolean hasToken = false;
                for (Map<String, String> lineToken : line) {
                    if (lineToken.get("isToken").equals(layoutToken)) {
                        String tokenValue = BeanUtils.getProperty(personName, lineToken.get("codeName"));
                        nameLabel = nameSupport.getNameMappings().get(lineToken.get("codeName"));
                        if (StringUtils.isNotBlank(tokenValue)) {
                            hasToken = true;
                            nameLine.append(tokenValue);
                        }
                    } else {
                        nameLine.append(lineToken.get("displayText"));
                    }
                }
                // only display a line if there's a token within it we've been able to resolve
                if (StringUtils.isNotBlank(nameLine.toString()) && hasToken) {
                    nameFields.put(nameLabel, nameLine.toString());
                }
            }
            return nameFields;
        } catch (Exception e) {
            throw new APIException("Unable to generate name fields for patient header", e);
        }
    }
}
