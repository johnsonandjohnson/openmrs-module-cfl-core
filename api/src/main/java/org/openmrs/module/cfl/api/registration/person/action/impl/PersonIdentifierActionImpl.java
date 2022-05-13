/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.registration.person.action.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.registration.person.action.AfterPersonCreatedAction;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;

import java.util.Map;

public class PersonIdentifierActionImpl implements AfterPersonCreatedAction {

    private static final String NEW_PERSON_IDENTIFIER = "New person identifier";

    private PersonService personService;

    private AdministrationService administrationService;

    private IdentifierSourceService identifierSourceService;

    /**
     * Method which will be called after creating a person.
     *
     * @param created - the person who is being created
     * @param submittedParameters - parameters, typically from an HttpServletRequest
     */
    @Override
    public void afterCreated(Person created, Map<String, String[]> submittedParameters) {
        PersonAttributeType type = getPersonIdentifierAttributeType();
        PersonAttribute identifier = new PersonAttribute(type, generateNewPersonIdentifierValue());
        created.addAttribute(identifier);
        personService.savePerson(created);
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setAdministrationService(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    public void setIdentifierSourceService(IdentifierSourceService identifierSourceService) {
        this.identifierSourceService = identifierSourceService;
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
            type = personService.getPersonAttributeTypeByUuid(identifierAttributeTypeUUID);
        }
        return type;
    }

    /**
     * Generates the new person identifier value based on global settings configuration
     *
     * @return - identifier value
     */
    private String generateNewPersonIdentifierValue() {
        String identifierSourceSetting = getIdentifierSourceSetting();
        IdentifierSource identifierSource = getSource(identifierSourceSetting);
        return identifierSourceService.generateIdentifier(identifierSource, NEW_PERSON_IDENTIFIER);
    }

    /**
     * Returns the person identifier source. Expect the identifier source id or uuid as a sourceIdentifier parameter.
     *
     * @param sourceIdentifier - the identifier source id (id or uuid)
     * @return - the fetched identifier source
     * @throws APIException - thrown when identifier source doesn't exists
     */
    private IdentifierSource getSource(String sourceIdentifier) throws APIException {
        IdentifierSource identifierSource;
        if (StringUtils.isNumeric(sourceIdentifier)) {
            identifierSource = identifierSourceService.getIdentifierSource(Integer.valueOf(sourceIdentifier));
        } else {
            identifierSource = identifierSourceService.getIdentifierSourceByUuid(sourceIdentifier);
        }
        if (identifierSource == null) {
            throw new APIException("Cannot find identifier source with id:" + sourceIdentifier);
        }
        return identifierSource;
    }

    /**
     * Returns the settings for person identifier attribute type.
     *
     * @return - setting value
     * @throws APIException - thrown when setting is blank
     */
    private String getPersonIdentifierTypeSetting() throws APIException  {
        String type = administrationService.getGlobalProperty(CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_KEY);
        if (StringUtils.isBlank(type)) {
            throw new APIException(String.format("Cannot find person attribute type for identifier. "
                    + "Verify %s global setting.", CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_KEY));
        }
        return type;
    }

    /**
     * Returns the settings for person identifier source.
     *
     * @return - setting value
     * @throws APIException - thrown when setting is blank
     */
    private String getIdentifierSourceSetting() throws APIException {
        String source = administrationService.getGlobalProperty(CFLConstants.PERSON_IDENTIFIER_SOURCE_KEY);
        if (StringUtils.isBlank(source)) {
            throw new APIException(String.format("Cannot find identifier source. "
                    + "Verify %s global setting.", CFLConstants.PERSON_IDENTIFIER_SOURCE_KEY));
        }
        return source;
    }
}
