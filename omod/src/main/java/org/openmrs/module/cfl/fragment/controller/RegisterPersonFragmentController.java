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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.cfl.api.dto.RelationshipDTOsBuilder;
import org.openmrs.module.cfl.api.registration.person.action.AfterPersonCreatedAction;
import org.openmrs.module.cfl.api.service.RelationshipService;
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
import java.util.Map;

import static org.openmrs.module.cfl.CFLRegisterPersonConstants.AFTER_CREATED_ACTIONS_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.AFTER_CREATED_URL_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.APP_ID_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.BIRTH_DATE_MONTHS_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.BIRTH_DATE_YEARS_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.DASHBOARD_LINK_ID_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.OTHER_PERSON_UUID_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_ADDRESS_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_NAME_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.PERSON_SERVICE_PROP;
import static org.openmrs.module.cfl.CFLRegisterPersonConstants.RELATIONSHIP_TYPE_PROP;

/**
 * Based on openmrs-module-registrationapp v1.13.0
 * omod/src/main/java/org/openmrs/module/registrationapp/fragment/controller/RegisterPatientFragmentController.java
 */
public class RegisterPersonFragmentController {

    private static final Log LOGGER = LogFactory.getLog(RegisterPersonFragmentController.class);

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
        resolvePersonRelationships(person, request.getParameterMap());
        performAfterPersonCreatedActions(person, app, request.getParameterMap());
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

    private void resolvePersonRelationships(Person person, Map<String, String[]> parameterMap) {
        if (person != null && parameterMap.containsKey(RELATIONSHIP_TYPE_PROP)
                && parameterMap.containsKey(OTHER_PERSON_UUID_PROP)) {
            String[] relationshipsTypes = parameterMap.get(RELATIONSHIP_TYPE_PROP);
            String[] otherPeopleUUIDs = parameterMap.get(OTHER_PERSON_UUID_PROP);
            getCflRelationshipService().createNewRelationships(
                    new RelationshipDTOsBuilder().withRelationshipsTypes(relationshipsTypes)
                            .withOtherPeopleUUIDs(otherPeopleUUIDs).build(), person);
        }
    }

    /**
     * Performs all actions that are configured to run after creating a person,
     * for a particular instance of the registration app
     *
     * @param person - related person already exists in the database
     * @param app - the app description - used to fetch config
     * @param parameterMap - parameters, typically from an HttpServletRequest
     */
    private void performAfterPersonCreatedActions(Person person, AppDescriptor app, Map<String, String[]> parameterMap) {
        ArrayNode afterCreatedArray = (ArrayNode) app.getConfig().get(AFTER_CREATED_ACTIONS_PROP);
        if (afterCreatedArray != null) {
            for (JsonNode actionNode : afterCreatedArray) {
                String beanName = actionNode.getTextValue();
                try {
                    AfterPersonCreatedAction action = Context.getRegisteredComponent(beanName,
                            AfterPersonCreatedAction.class);
                    action.afterCreated(person, parameterMap);
                } catch (APIException ex) {
                    LOGGER.warn(String.format("Error occurred during executing after "
                            + "creating person action from bean: %s ", beanName));
                }
            }
        }
    }

    /**
     * Returns the CFL relationship service based on the actual application context.
     *
     * @return - person service
     */
    private RelationshipService getCflRelationshipService() {

        return Context.getRegisteredComponent("cfl.relationshipService", RelationshipService.class);
    }

}
