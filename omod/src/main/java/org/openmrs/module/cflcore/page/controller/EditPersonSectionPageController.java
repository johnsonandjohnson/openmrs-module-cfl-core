/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.page.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.cflcore.form.RegisterPersonFormBuilder;
import org.openmrs.module.cflcore.util.AppUiUtils;
import org.openmrs.module.registrationapp.AddressSupportCompatibility;
import org.openmrs.module.registrationapp.NameSupportCompatibility;
import org.openmrs.module.registrationapp.model.NavigableFormStructure;
import org.openmrs.module.registrationapp.model.Section;
import org.openmrs.module.registrationcore.RegistrationCoreUtil;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.validator.PersonValidator;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.ADDRESS_TEMPLATE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.APP_ID_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.APP_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.BIRTH_DATE_MONTHS_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.BIRTH_DATE_YEARS_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.ENABLE_OVERRIDE_PATH;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.ENABLE_OVERRIDE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.MESSAGE_SOURCE_SERVICE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.NAME_TEMPLATE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.PERSON_ID_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.PERSON_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.PERSON_SERVICE_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.PERSON_VALIDATOR_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.RETURN_URL_PROP;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.SECTION;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.SECTION_ID;
import static org.openmrs.module.cflcore.CFLRegisterPersonConstants.UI_UTILS_PROP;

public class EditPersonSectionPageController {

    private static final Log LOGGER = LogFactory.getLog(EditPersonSectionPageController.class);

    public void get(UiSessionContext sessionContext, PageModel model, @RequestParam(PERSON_ID_PROP) Person person,
                    @RequestParam(APP_ID_PROP) AppDescriptor app,
                    @RequestParam(value = RETURN_URL_PROP, required = false) String returnUrl,
                    @RequestParam(SECTION_ID) String sectionId) {
        sessionContext.requireAuthentication();
        NavigableFormStructure formStructure = RegisterPersonFormBuilder.buildFormStructure(app);
        addModelAttributes(model, person, formStructure.getSections().get(sectionId), returnUrl, app);
    }

    @SuppressWarnings({"checkstyle:ParameterNumber", "PMD.ExcessiveParameterList", "checkstyle:ParameterAssignment",
            "PMD.AvoidReassigningParameters"})
    public String post(UiSessionContext sessionContext, PageModel model,
                       @RequestParam(PERSON_ID_PROP) @BindParams Person person, @BindParams PersonAddress address,
                       @BindParams PersonName name,
                       @RequestParam(value = BIRTH_DATE_YEARS_PROP, required = false) Integer birthdateYears,
                       @RequestParam(value = BIRTH_DATE_MONTHS_PROP, required = false) Integer birthdateMonths,
                       @RequestParam(APP_ID_PROP) AppDescriptor app, @RequestParam(SECTION_ID) String sectionId,
                       @RequestParam(RETURN_URL_PROP) String returnUrl,
                       @SpringBean(PERSON_SERVICE_PROP) PersonService personService,
                       @SpringBean(MESSAGE_SOURCE_SERVICE_PROP) MessageSourceService messageSourceService, Session session,
                       @SpringBean(PERSON_VALIDATOR_PROP) PersonValidator personValidator, UiUtils ui,
                       HttpServletRequest request) {

        sessionContext.requireAuthentication();
        handlePersonName(person, name);
        handleBirthDate(person, birthdateYears, birthdateMonths);
        handlePersonAddress(person, address);
        NavigableFormStructure formStructure = RegisterPersonFormBuilder.buildFormStructure(app);
        BindingResult errors = new BeanPropertyBindingResult(person, PERSON_PROP);
        personValidator.validate(person, errors);
        RegisterPersonFormBuilder.resolvePersonAttributeFields(formStructure, person, request.getParameterMap());
        if (errors.hasErrors()) {
            createErrorMessage(ui, model, messageSourceService, session, errors);
        } else {
            try {
                final Person savedPerson = personService.savePerson(person);

                final String sectionLabel = ui.message(formStructure.getSections().get(sectionId).getLabel());
                final String flashMessage = ui.message("registrationapp.editCustomSectionInfoMessage.success",
                        savedPerson.getPersonName() != null ? ui.encodeHtml(savedPerson.getPersonName().toString()) : "",
                        sectionLabel);

                flashInfoMessage(ui, request.getSession(), flashMessage);

                return "redirect:" + returnUrl;
            } catch (Exception e) {
                LOGGER.warn("Error occurred while saving patient's contact info", e);
                session.setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, "registrationapp.save.fail");
            }
        }

        addModelAttributes(model, person, formStructure.getSections().get(sectionId), returnUrl, app);
        return null;
    }

    /**
     * Updates the person name, if new name is present. In case od update the old name will be voided.
     *
     * @param person - person which name should be updated
     * @param name   - a new name value
     */
    private void handlePersonName(Person person, PersonName name) {
        if (person.getPersonName() != null && name != null && StringUtils.isNotBlank(name.getFullName())) {
            PersonName currentName = person.getPersonName();
            if (!currentName.equalsContent(name)) {
                person.addName(name);
                currentName.setVoided(true);
            }
        }
    }

    /**
     * Updates the person birthdate estimate, in case when birthdate does not set byt but estimate date present exists.
     *
     * @param person          - person which name should be updated
     * @param birthdateYears  - the value of estimated years
     * @param birthdateMonths - the value of estimated months
     */
    private void handleBirthDate(Person person, Integer birthdateYears, Integer birthdateMonths) {
        if (person.getBirthdate() == null && (birthdateYears != null || birthdateMonths != null)) {
            person.setBirthdateEstimated(true);
            person.setBirthdate(RegistrationCoreUtil.calculateBirthdateFromAge(birthdateYears, birthdateMonths, null, null));
        }
    }

    /**
     * Updates the person address, if present. In case od update the old address will be voided.
     *
     * @param person  - person which name should be updated
     * @param address - a new person address
     */
    private void handlePersonAddress(@BindParams @RequestParam(PERSON_ID_PROP) Person person,
                                     @BindParams PersonAddress address) {
        if (address != null && !address.isBlank()) {
            PersonAddress currentAddress = person.getPersonAddress();
            if (currentAddress != null) {
                if (!currentAddress.equalsContent(address)) {
                    person.addAddress(address);
                    currentAddress.setVoided(true);
                }
            } else {
                person.addAddress(address);
            }
        }
    }

    private void addModelAttributes(PageModel model, Person person, Section section, String returnUrl, AppDescriptor app) {

        NameSupportCompatibility nameSupport =
                Context.getRegisteredComponent(NameSupportCompatibility.ID, NameSupportCompatibility.class);
        AddressSupportCompatibility addressSupport =
                Context.getRegisteredComponent(AddressSupportCompatibility.ID, AddressSupportCompatibility.class);

        model.addAttribute(APP_PROP, app);
        model.addAttribute(RETURN_URL_PROP, returnUrl);
        model.put(UI_UTILS_PROP, new AppUiUtils());
        model.addAttribute(PERSON_PROP, person);
        model.addAttribute(ADDRESS_TEMPLATE_PROP, addressSupport.getAddressTemplate());
        model.addAttribute(NAME_TEMPLATE_PROP, nameSupport.getDefaultLayoutTemplate());
        model.addAttribute(SECTION, section);
        model.addAttribute(ENABLE_OVERRIDE_PROP,
                Context.getAdministrationService().getGlobalProperty(ENABLE_OVERRIDE_PATH, Boolean.FALSE.toString()));
    }

    private void createErrorMessage(UiUtils ui, PageModel model, MessageSourceService messageSourceService, Session session,
                                    BindingResult errors) {
        model.addAttribute("errors", errors);

        final StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(ui.encodeHtml(messageSourceService.getMessage("error.failed.validation")));
        errorMessage.append("<ul>");

        for (final ObjectError error : errors.getAllErrors()) {
            final String message =
                    messageSourceService.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), null);
            errorMessage.append("<li>");
            errorMessage.append(ui.encodeHtml(message));
            errorMessage.append("</li>");
        }

        errorMessage.append("</ul>");

        session.setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, errorMessage.toString());
    }

    private void flashInfoMessage(UiUtils ui, HttpSession session, String message) {
        session.setAttribute("emr.infoMessage", ui.encodeHtml(message));
        session.setAttribute("emr.toastMessage", "true");
    }
}
