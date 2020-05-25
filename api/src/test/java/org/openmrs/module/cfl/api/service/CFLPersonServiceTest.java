package org.openmrs.module.cfl.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.contract.CFLPerson;
import org.openmrs.module.cfl.api.helper.PersonHelper;
import org.openmrs.module.cfl.builder.PersonAttributeBuilder;
import org.openmrs.module.cfl.builder.PersonNameBuilder;
import org.openmrs.module.cfl.builder.PersonAttributeTypeBuilder;
import org.openmrs.module.cfl.builder.PersonBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CFLPersonServiceTest {

    @Mock
    private CFLPersonService cflPersonService;

    @Mock
    private PersonDAO personDAO;

    private Person person;

    private PersonAttribute phoneNumberAttr;

    private PersonAttribute personStatusAttr;

    private List<CFLPerson> cflPeople;

    @Before
    public void setUp() {
      person = buildPerson();
      cflPeople = prepareCflPeople();
      when(personDAO.getPerson(person.getId())).thenReturn(person);
      when(cflPersonService.findByPhone(Constant.PHONE_NUMBER, false)).thenReturn(cflPeople);
    }

    @Test
    public void shouldSavePhoneNumberAttribute() {
        phoneNumberAttr = buildPhoneNumberAttribute();
        person.addAttribute(phoneNumberAttr);
        cflPersonService.savePersonAttribute(person.getId(),
                Constant.PHONE_NUMBER_ATTRIBUTE_NAME, Constant.PHONE_NUMBER);

        PersonAttribute personAttribute = personDAO.getPerson(person.getId())
                .getAttribute(Constant.PHONE_NUMBER_ATTRIBUTE_NAME);

        assertNotNull(personAttribute);
        assertEquals(personAttribute.getValue(), Constant.PHONE_NUMBER);
    }

    @Test
    public void shouldSavePersonStatusAttribute() {
        personStatusAttr = buildPersonStatusAttribute();
        person.addAttribute(personStatusAttr);
        cflPersonService.savePersonAttribute(person.getId(),
                Constant.PERSON_STATUS_ATTRIBUTE_NAME, Constant.ACTIVATED_PERSON_STATUS);

        PersonAttribute personAttribute = personDAO.getPerson(person.getId())
                .getAttribute(Constant.PERSON_STATUS_ATTRIBUTE_NAME);

        assertNotNull(personAttribute);
        assertEquals(personAttribute.getValue(), Constant.ACTIVATED_PERSON_STATUS);
    }

    @Test
    public void shouldFindPeopleByPhone() {
        List<CFLPerson> people = cflPersonService.findByPhone(Constant.PHONE_NUMBER, false);

        assertNotNull(people);
        assertEquals(2, people.size());
        assertTrue(PersonHelper.containsPerson(people, PersonHelper.JOHN_GIVEN_NAME + " " + PersonHelper.JOHN_FAMILY_NAME));
        assertTrue(PersonHelper.containsPerson(people, PersonHelper.ADAM_GIVEN_NAME + " " + PersonHelper.ADAM_FAMILY_NAME));
        assertFalse(people.get(0).isCaregiver());
        assertTrue(people.get(1).isCaregiver());
    }

    private List<CFLPerson> prepareCflPeople() {
        List<CFLPerson> poeple = new ArrayList<CFLPerson>();

        Person person1 = new PersonBuilder()
                .withName(new PersonNameBuilder().withGiven(PersonHelper.JOHN_GIVEN_NAME)
                        .withFamily(PersonHelper.JOHN_FAMILY_NAME).build())
                .build();
        Person person2 = new PersonBuilder()
                .withName(new PersonNameBuilder().withGiven(PersonHelper.ADAM_GIVEN_NAME)
                        .withFamily(PersonHelper.ADAM_FAMILY_NAME).build())
                .build();

        PersonAttribute person1PhoneNumber = new PersonAttributeBuilder()
                .withPerson(person1)
                .withValue(Constant.PHONE_NUMBER)
                .withPersonAttributeType(new PersonAttributeTypeBuilder().build())
                .build();
        person1.addAttribute(person1PhoneNumber);
        cflPersonService.savePersonAttribute(person.getId(),
                Constant.PHONE_NUMBER_ATTRIBUTE_NAME, Constant.PHONE_NUMBER);

        PersonAttribute person2PhoneNumber = new PersonAttributeBuilder()
                .withPerson(person2)
                .withValue(Constant.PHONE_NUMBER)
                .withPersonAttributeType(new PersonAttributeTypeBuilder().build())
                .build();
        person2.addAttribute(person2PhoneNumber);
        cflPersonService.savePersonAttribute(person2.getId(),
                Constant.PHONE_NUMBER_ATTRIBUTE_NAME, Constant.PHONE_NUMBER);

        poeple.add(new CFLPerson(person1, false));
        poeple.add(new CFLPerson(person2, true));

        return poeple;
    }

    private PersonAttribute buildPhoneNumberAttribute() {
        return new PersonAttributeBuilder()
                .withId(Constant.ATTR_ID_1)
                .withPerson(person)
                .withValue(Constant.PHONE_NUMBER)
                .withPersonAttributeType(new PersonAttributeTypeBuilder()
                        .withName(Constant.PHONE_NUMBER_ATTRIBUTE_NAME).build())
                .build();
    }

    private PersonAttribute buildPersonStatusAttribute() {
        return new PersonAttributeBuilder()
                .withId(Constant.ATTR_ID_2)
                .withPerson(person)
                .withValue(Constant.ACTIVATED_PERSON_STATUS)
                .withPersonAttributeType(new PersonAttributeTypeBuilder()
                        .withName(Constant.PERSON_STATUS_ATTRIBUTE_NAME).build())
                .build();
    }

    private Person buildPerson() {
        return new PersonBuilder()
                .withId(Constant.ATTR_ID_1)
                .withGender(Constant.MALE_GENDER)
                .withName(new PersonNameBuilder().build())
                .build();
    }

}
