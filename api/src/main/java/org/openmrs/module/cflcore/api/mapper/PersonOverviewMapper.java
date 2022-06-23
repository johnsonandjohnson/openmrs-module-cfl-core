/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.mapper;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.api.dto.PersonAttributeDTO;
import org.openmrs.module.cflcore.api.dto.PersonOverviewEntryDTO;
import org.openmrs.module.cflcore.api.exception.CflRuntimeException;
import org.openmrs.module.cflcore.api.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.api.context.Context.getAdministrationService;
import static org.openmrs.module.cflcore.CFLConstants.PERSON_IDENTIFIER_ATTRIBUTE_KEY;

public class PersonOverviewMapper extends AbstractMapper<PersonOverviewEntryDTO, Person> {

    @Override
    public PersonOverviewEntryDTO toDto(Person person) {
        PersonOverviewEntryDTO dto = new PersonOverviewEntryDTO();
        dto.setGender(person.getGender());
        dto.setAge(person.getAge());
        dto.setBirthdate(person.getBirthdate() != null
                ? DateUtil.convertToDateTimeWithZone(person.getBirthdate())
                : null);
        dto.setBirthdateEstimated(BooleanUtils.isTrue(person.getBirthdateEstimated()));
        dto.setPersonName(person.getPersonName() != null ? person.getPersonName().getFullName() : null);
        dto.setAttributes(getAttributes(person));
        dto.setPatientIdentifier(getPatientIdentifier(person));
        dto.setPersonIdentifier(getPersonIdentifier(person));
        dto.setUuid(person.getUuid());
        return dto;
    }

    @Override
    public Person fromDto(PersonOverviewEntryDTO dto) {
        throw new CflRuntimeException("Not implemented yet");
    }

    private String getPatientIdentifier(Person person) {
        if (person.isPatient()) {
            Patient patient = (Patient) person;
            return patient.getPatientIdentifier().toString();
        }
        return null;
    }

    private String getPersonIdentifier(Person person) {
        String personIdentifier = null;
        PersonAttributeType identifierAttributeType = getPersonIdentifierAttributeType();
        if (identifierAttributeType != null) {
            PersonAttribute attribute = person.getAttribute(identifierAttributeType);
            if (attribute != null) {
                personIdentifier = attribute.getValue();
            }
        }
        return personIdentifier;
    }

    private PersonAttributeType getPersonIdentifierAttributeType() {
        PersonAttributeType type = null;
        String attributeTypeUUID = getAdministrationService().getGlobalProperty(PERSON_IDENTIFIER_ATTRIBUTE_KEY);
        if (StringUtils.isNotBlank(attributeTypeUUID)) {
            type = Context.getPersonService().getPersonAttributeTypeByUuid(attributeTypeUUID);
        }
        return type;
    }

    private List<PersonAttributeDTO> getAttributes(Person person) {
        ArrayList<PersonAttributeDTO> dtos = new ArrayList<PersonAttributeDTO>();
        if (person.getAttributes() != null) {
            for (PersonAttribute attribute : person.getAttributes()) {
                if (!attribute.isVoided()) {
                    PersonAttributeDTO dto = new PersonAttributeDTO();
                    dto.setValue(attribute.getValue());
                    dto.setName(attribute.getAttributeType().getName());
                    dtos.add(dto);
                }
            }
        }
        return dtos;
    }
}
