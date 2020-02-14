package org.openmrs.module.cfl.api.mapper;

import org.apache.commons.lang3.BooleanUtils;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.module.cfl.api.dto.PersonAttributeDTO;
import org.openmrs.module.cfl.api.dto.PersonOverviewEntryDTO;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class PersonOverviewMapper extends AbstractMapper<PersonOverviewEntryDTO, Person> {

    @Override
    public PersonOverviewEntryDTO toDto(Person person) {
        PersonOverviewEntryDTO dto = new PersonOverviewEntryDTO();
        dto.setPersonId(person.getId());
        dto.setGender(person.getGender());
        dto.setAge(person.getAge());
        dto.setBirthdate(person.getBirthdate() != null
                ? DateUtil.convertToDateTimeWithZone(person.getBirthdate())
                : null);
        dto.setBirthdateEstimated(BooleanUtils.isTrue(person.getBirthdateEstimated()));
        dto.setPersonName(person.getPersonName() != null ? person.getPersonName().getFullName() : null);
        dto.setAttributes(getAttributes(person));
        dto.setUuid(person.getUuid());
        return dto;
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

    @Override
    public Person fromDto(PersonOverviewEntryDTO dto) {
        throw new CflRuntimeException("Not implemented yet");
    }
}
