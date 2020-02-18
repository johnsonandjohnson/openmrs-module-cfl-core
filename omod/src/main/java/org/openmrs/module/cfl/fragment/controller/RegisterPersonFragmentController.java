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

package org.openmrs.module.cfl.fragment.controller;

import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.PersonService;
import org.openmrs.module.appframework.domain.AppDescriptor;

import org.openmrs.module.cfl.form.RegisterPersonFormBuilder;
import org.openmrs.module.registrationapp.model.NavigableFormStructure;
import org.openmrs.module.registrationcore.RegistrationCoreUtil;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import static org.openmrs.module.cfl.CFLRegisterPersonConstants.AFTER_CREATED_URL_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.APP_ID_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.BIRTH_DATE_MONTHS_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.BIRTH_DATE_YEARS_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.DASHBOARD_LINK_ID_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_ADDRESS_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_NAME_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_SERVICE_PROP;

/**
 * Based on openmrs-module-registrationapp v1.13.0
 * omod/src/main/java/org/openmrs/module/registrationapp/fragment/controller/RegisterPatientFragmentController.java
 */
public class RegisterPersonFragmentController {

    @SuppressWarnings({"checkstyle:ParameterNumber", "PMD.ExcessiveParameterList",
            "checkstyle:ParameterAssignment", "PMD.AvoidReassigningParameters"})
    public FragmentActionResult submit(@RequestParam(value = APP_ID_PROP) AppDescriptor app,
                                       @ModelAttribute(PERSON_PROP) @BindParams Person person,
                                       @ModelAttribute(PERSON_NAME_PROP) @BindParams PersonName name,
                                       @ModelAttribute(PERSON_ADDRESS_PROP) @BindParams PersonAddress address,
                                       @RequestParam(value = BIRTH_DATE_YEARS_PROP,
                                               required = false) Integer birthdateYears,
                                       @RequestParam(value = BIRTH_DATE_MONTHS_PROP,
                                               required = false) Integer birthdateMonths,
                                       @SpringBean(PERSON_SERVICE_PROP) PersonService personService,
                                       HttpServletRequest request) {
        person.addName(name);
        person.addAddress(address);
        handleBirthDate(person, birthdateYears, birthdateMonths);

        NavigableFormStructure formStructure = RegisterPersonFormBuilder.buildFormStructure(app);
        RegisterPersonFormBuilder.resolvePersonAttributeFields(formStructure, person, request.getParameterMap());

        person = personService.savePerson(person);
        return new SuccessResult(getRedirectUrl(person, app));
    }

    private String getRedirectUrl(Person person, AppDescriptor app) {
        String redirectUrl = app.getConfig().get(AFTER_CREATED_URL_PROP).getTextValue();
        redirectUrl = redirectUrl.replaceAll("\\{\\{" + DASHBOARD_LINK_ID_PROP + "\\}\\}", person.getUuid());

        return redirectUrl;
    }

    private void handleBirthDate(Person person, Integer birthdateYears, Integer birthdateMonths) {
        if (isEstimationOfBirthDateNeeded(person) &&
                isEstimationOfBirthDatePossible(birthdateYears, birthdateMonths)) {
            person.setBirthdateEstimated(true);
            person.setBirthdate(
                    RegistrationCoreUtil.calculateBirthdateFromAge(
                            birthdateYears, birthdateMonths, null, null));
        }
    }

    private boolean isEstimationOfBirthDateNeeded(Person person) {
        return person.getBirthdate() == null;
    }

    private boolean isEstimationOfBirthDatePossible(Integer birthdateYears, Integer birthdateMonths) {
        return birthdateYears != null || birthdateMonths != null;
    }

}
