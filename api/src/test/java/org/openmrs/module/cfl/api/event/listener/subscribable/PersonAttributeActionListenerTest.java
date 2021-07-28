/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api.event.listener.subscribable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.api.PersonService;
import org.openmrs.event.Event;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.exception.CflRuntimeException;
import org.openmrs.module.cfl.api.service.PersonAttributeListenerService;
import org.openmrs.test.BaseContextMockTest;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class PersonAttributeActionListenerTest extends BaseContextMockTest {

    @Mock
    private PersonService personService;

    @Mock
    private MapMessage message;

    @Mock
    private PersonAttributeListenerService personAttributeListenerService;

    @InjectMocks
    private PersonAttributeActionListener personAttributeActionListener;

    @Before
    public void setUp() throws JMSException {
        when(message.getString(CFLConstants.ACTION_KEY)).thenReturn(Event.Action.UPDATED.name());
        contextMockHelper.setService(PersonService.class, personService);
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionForInvalidEventMessage() {
        //When
        try {
            personAttributeActionListener.performAction(null);
            fail("should throw CflRuntimeException: Event message has to be MapMessage");
        } catch (CflRuntimeException e) {
            //Then
            verifyZeroInteractions(personAttributeListenerService);
        }
    }

    @Test
    public void performAction_shouldThrowIllegalArgumentExceptionIfUnknownAction() throws JMSException {
        //When
        try {
            when(message.getString(CFLConstants.ACTION_KEY)).thenReturn("wrong");
            personAttributeActionListener.performAction(message);
            fail("should throw CflRuntimeException: Unable to retrieve event action for name:");
        } catch (CflRuntimeException e) {
            //Then
            verifyZeroInteractions(personAttributeListenerService);
        }
    }

    @Test
    public void performAction_shouldThrowCflRuntimeExceptionIfPersonNotFound() throws JMSException {
        //When
        try {
            when(message.getString(CFLConstants.UUID_KEY)).thenReturn("wrong");
            personAttributeActionListener.performAction(message);
            fail("should throw CflRuntimeException: Unable to retrieve person attribute by uuid");
        } catch (CflRuntimeException e) {
            //Then
            verify(personService, times(1)).getPersonAttributeByUuid(Mockito.anyString());
            verifyZeroInteractions(personAttributeListenerService);
        }
    }
}
