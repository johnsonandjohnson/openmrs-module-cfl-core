/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.service;

import org.hibernate.Criteria;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.contract.CFLPerson;
import org.openmrs.module.cfl.api.helper.PersonHelper;
import org.openmrs.module.cfl.api.service.impl.CFLPersonServiceImpl;
import org.openmrs.module.cfl.builder.PersonAttributeBuilder;
import org.openmrs.module.cfl.builder.PersonAttributeTypeBuilder;
import org.openmrs.module.cfl.builder.PersonBuilder;
import org.openmrs.module.cfl.builder.PersonNameBuilder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Context.class, Daemon.class} )
public class CFLPersonServiceTest {

    @InjectMocks
    private CFLPersonServiceImpl cflPersonService;

    @Mock
    private PersonDAO personDAO;

    @Mock
    private DbSessionFactory sessionFactory;

    @Mock
    private DbSession dbSession;

    @Mock
    private Criteria criteria;

    private Person person;

    private PersonAttributeType phoneAttributeType;

    private PersonAttributeType personStatusType;

    @Before
    public void setUp() {
        mockStatic(Context.class);

        person = buildPerson(1, "John", "Test");

        phoneAttributeType = buildPersonAttributeType(Constant.PHONE_NUMBER_ATTRIBUTE_NAME);
        personStatusType = buildPersonAttributeType(Constant.PERSON_STATUS_ATTRIBUTE_NAME);
        when(personDAO.getPerson(anyInt())).thenReturn(person);
        when(sessionFactory.getCurrentSession()).thenReturn(dbSession);
        when(dbSession.createCriteria(PersonAttribute.class)).thenReturn(criteria);
    }

    @Test
    public void shouldSavePhoneNumberAttribute() {
        PersonAttribute phoneAttribute = buildPersonAttribute(person, phoneAttributeType, Constant.PHONE_NUMBER);
        person.addAttribute(phoneAttribute);
        when(personDAO.getPersonAttributeTypes(any(), any(), any(), any()))
                .thenReturn(Arrays.asList(phoneAttributeType));

        cflPersonService.savePersonAttribute(person.getId(),
                Constant.PHONE_NUMBER_ATTRIBUTE_NAME, Constant.PHONE_NUMBER);

        PersonAttribute personAttribute = personDAO.getPerson(person.getId())
                .getAttribute(Constant.PHONE_NUMBER_ATTRIBUTE_NAME);

        assertNotNull(personAttribute);
        assertEquals(personAttribute.getAttributeType().getName(), Constant.PHONE_NUMBER_ATTRIBUTE_NAME);
        assertEquals(personAttribute.getValue(), Constant.PHONE_NUMBER);
    }

    @Test
    public void shouldSavePersonStatusAttribute() {
        PersonAttribute statusAttribute = buildPersonAttribute(person, personStatusType,
                Constant.ACTIVATED_PERSON_STATUS);
        person.addAttribute(statusAttribute);
        when(personDAO.getPersonAttributeTypes(any(), any(), any(), any()))
                .thenReturn(Arrays.asList(personStatusType));

        cflPersonService.savePersonAttribute(person.getId(),
                Constant.PERSON_STATUS_ATTRIBUTE_NAME, Constant.ACTIVATED_PERSON_STATUS);

        PersonAttribute personAttribute = personDAO.getPerson(person.getId())
                .getAttribute(Constant.PERSON_STATUS_ATTRIBUTE_NAME);

        assertNotNull(personAttribute);
        assertEquals(personAttribute.getAttributeType().getName(), Constant.PERSON_STATUS_ATTRIBUTE_NAME);
        assertEquals(personAttribute.getValue(), Constant.ACTIVATED_PERSON_STATUS);
    }

    @Test
    public void shouldFindPeopleByPhone() {
        buildTestData();

        List<CFLPerson> people = cflPersonService.findByPhone(Constant.PHONE_NUMBER, false);

        assertNotNull(people);
        assertEquals(2, people.size());
        assertTrue(PersonHelper.containsPerson(people,
                PersonHelper.JOHN_GIVEN_NAME + " " + PersonHelper.JOHN_FAMILY_NAME));
        assertTrue(PersonHelper.containsPerson(people,
                PersonHelper.ADAM_GIVEN_NAME + " " + PersonHelper.ADAM_FAMILY_NAME));
        for (CFLPerson person : people)  {
            assertFalse(person.isCaregiver());
        }
    }

    private void buildTestData() {
        Person person1 = buildPerson(Constant.ATTR_ID_1, PersonHelper.JOHN_GIVEN_NAME, PersonHelper.JOHN_FAMILY_NAME);
        PersonAttribute phoneAttr1 = buildPersonAttribute(person1, phoneAttributeType, Constant.PHONE_NUMBER);
        person1.addAttribute(buildPersonAttribute(person1, phoneAttributeType, Constant.PHONE_NUMBER));

        Person person2 = buildPerson(Constant.ATTR_ID_2, PersonHelper.ADAM_GIVEN_NAME, PersonHelper.ADAM_FAMILY_NAME);
        PersonAttribute phoneAttr2 = buildPersonAttribute(person2, phoneAttributeType, Constant.PHONE_NUMBER);
        person2.addAttribute(buildPersonAttribute(person2, phoneAttributeType, Constant.PHONE_NUMBER));

        when(criteria.list()).thenReturn(Arrays.asList(phoneAttr1, phoneAttr2));
    }

    private Person buildPerson(Integer id, String givenName, String familyName) {
        return new PersonBuilder()
                .withId(id)
                .withName(new PersonNameBuilder()
                        .withGiven(givenName)
                        .withFamily(familyName).build())
                .build();
    }

    private PersonAttribute buildPersonAttribute(Person person, PersonAttributeType attributeType, String value) {
        return new PersonAttributeBuilder()
                .withPerson(person)
                .withPersonAttributeType(attributeType)
                .withValue(value)
                .build();
    }

    private PersonAttributeType buildPersonAttributeType(String attrTypeName) {
        return new PersonAttributeTypeBuilder().withName(attrTypeName).build();
    }
}
