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

package org.openmrs.module.cflcore.fragment.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.coreapps.CoreAppsProperties;
import org.openmrs.module.coreapps.contextmodel.PatientContextModel;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ideally you pass in a PatientDomainWrapper as the "patient" config parameter. But if you pass in
 * a Patient, then this controller will wrap that for you.
 */
public class PatientHeaderFragmentController extends HeaderFragment {

    private static final String TELEPHONE = "telephone";
    private static final String SHOW_GENDER_PERSON_HEADER = "showGenderPersonHeader";
    private static final String SHOW_AGE_PERSON_HEADER = "showAgePersonHeader";

    @SuppressWarnings({"checkstyle:ParameterNumber", "checkstyle:ParameterAssignment",
            "PMD.ExcessiveParameterList", "PMD.AvoidReassigningParameters"})
    public void controller(FragmentConfiguration config, @SpringBean("emrApiProperties") EmrApiProperties emrApiProperties,
                           @SpringBean("coreAppsProperties") CoreAppsProperties coreAppsProperties,
                           @SpringBean("baseIdentifierSourceService") IdentifierSourceService identifierSourceService,
                           @FragmentParam(required = false, value = "appContextModel") AppContextModel appContextModel,
                           @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
                           @FragmentParam("patient") Object patient, @InjectBeans PatientDomainWrapper wrapper,
                           @SpringBean("adtService") AdtService adtService, UiSessionContext sessionContext,
                           UiUtils uiUtils,
                           FragmentModel model) {

        if (patient instanceof Patient) {
            wrapper.setPatient((Patient) patient);
        } else {
            wrapper = (PatientDomainWrapper) patient;
        }
        config.addAttribute("patient", wrapper);
        config.addAttribute("patientNames", getNames(wrapper.getPersonName()));
        config.addAttribute("shipId", getShipIdentifier(wrapper.getPatient()));
        config.addAttribute(TELEPHONE, wrapper.getPatient().getPerson()
                .getAttribute(CFLConstants.TELEPHONE_ATTRIBUTE_NAME));

        if (appContextModel == null) {
            AppContextModel contextModel = sessionContext.generateAppContextModel();
            contextModel.put("patient", new PatientContextModel(wrapper.getPatient()));
            model.addAttribute("appContextModel", contextModel);
        }

        List<Extension> firstLineFragments = appFrameworkService
                .getExtensionsForCurrentUser("patientHeader.firstLineFragments");
        Collections.sort(firstLineFragments);
        model.addAttribute("firstLineFragments", firstLineFragments);

        List<Extension> secondLineFragments = appFrameworkService
                .getExtensionsForCurrentUser("patientHeader.secondLineFragments");
        Collections.sort(secondLineFragments);
        model.addAttribute("secondLineFragments", secondLineFragments);

        List<ExtraPatientIdentifierType> extraPatientIdentifierTypes = new ArrayList<>();

        for (PatientIdentifierType type : emrApiProperties.getExtraPatientIdentifierTypes()) {
            List<AutoGenerationOption> options = identifierSourceService.getAutoGenerationOptions(type);
            // TODO note that this may allow use to edit a identifier that should not be editable, or vice versa,
            //  in the rare case where there are multiple autogeneration
            // TODO options for a single identifier type (which is possible if you have multiple locations)
            //  and the manual entry boolean is different between those two generators
            extraPatientIdentifierTypes.add(new ExtraPatientIdentifierType(type,
                    options.size() > 0 ? options.get(0).isManualEntryEnabled() : true));
        }

        config.addAttribute("extraPatientIdentifierTypes", extraPatientIdentifierTypes);
        config.addAttribute("extraPatientIdentifiersMappedByType",
                wrapper.getExtraIdentifiersMappedByType(sessionContext.getSessionLocation()));
        config.addAttribute("dashboardUrl", coreAppsProperties.getDashboardUrl());
        model.addAttribute(SHOW_GENDER_PERSON_HEADER, getShowGenderPersonHeader());
        model.addAttribute(SHOW_AGE_PERSON_HEADER, getShowAgePersonHeader());
    }

    private String getShipIdentifier(Patient patient) {
        PatientIdentifier shipIdentifier = patient.getPatientIdentifier("SHIP ID");
        if (shipIdentifier == null) {
            return "-";
        } else {
            return shipIdentifier.getIdentifier();
        }
    }
    
    private Boolean getShowGenderPersonHeader() {
        return StringUtils.equalsIgnoreCase(
            Context.getAdministrationService()
                .getGlobalProperty(CFLConstants.SHOW_GENDER_PERSON_HEADER_KEY),
            "true");
    }
    
    private Boolean getShowAgePersonHeader() {
        return StringUtils.equalsIgnoreCase(
            Context.getAdministrationService()
                .getGlobalProperty(CFLConstants.SHOW_AGE_PERSON_HEADER_KEY),
            "true");
    }

    public class ExtraPatientIdentifierType {

        private PatientIdentifierType patientIdentifierType;

        private boolean editable;

        public ExtraPatientIdentifierType(PatientIdentifierType type, boolean editable) {
            this.patientIdentifierType = type;
            this.editable = editable;
        }

        public PatientIdentifierType getPatientIdentifierType() {
            return patientIdentifierType;
        }

        public void setPatientIdentifierType(PatientIdentifierType patientIdentifierType) {
            this.patientIdentifierType = patientIdentifierType;
        }

        public boolean isEditable() {
            return editable;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }
    }

}
