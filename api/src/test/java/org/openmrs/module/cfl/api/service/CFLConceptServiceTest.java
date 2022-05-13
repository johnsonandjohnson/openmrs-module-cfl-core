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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.cfl.api.helper.ConceptHelper;
import org.openmrs.module.cfl.api.service.impl.CFLConceptServiceImpl;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, Daemon.class})
public class CFLConceptServiceTest {

    private static final String SMS_WELCOME_MSG_CONCEPT_NAME = "whatsappTemplate-onboardingMessage";

    @InjectMocks
    private CFLConceptServiceImpl cflConceptService;

    @Mock
    private ConceptService conceptService;

    private Concept concept;

    @Before
    public void setUp() {
        mockStatic(Context.class);
        concept = ConceptHelper.buildConcept(SMS_WELCOME_MSG_CONCEPT_NAME);
    }

    @Test
    public void shouldReturnEnglishMsg() {
        when(conceptService.getConceptByName(anyString())).thenReturn(concept);
        String actual = cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "eng");
        Assert.assertEquals("Hello patient", actual);
    }

    @Test
    public void shouldReturnDutchMsg() {
        when(conceptService.getConceptByName(anyString())).thenReturn(concept);
        String actual = cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "nld");
        Assert.assertEquals("Hallo patiënt", actual);
    }

    @Test
    public void shouldReturnSpanishMsg() {
        when(conceptService.getConceptByName(anyString())).thenReturn(concept);
        String actual = cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "spa");
        Assert.assertEquals("Hola paciente", actual);
    }

    @Test
    public void shouldReturnNullIfConceptIsNull() {
        concept = null;
        when(conceptService.getConceptByName(anyString())).thenReturn(concept);
        String actual = cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "spa");
        Assert.assertNull(actual);
    }
}
