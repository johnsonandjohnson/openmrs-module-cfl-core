package org.openmrs.module.cfl.api.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.cfl.api.helper.ConceptHelper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, Daemon.class})
public class CFLConceptServiceTest {

    @Mock
    private ConceptService conceptService;

    @Mock
    private CFLConceptService cflConceptService;

    private Concept concept;

    private static final String SMS_WELCOME_MSG_CONCEPT_NAME = "whatsappTemplate-onboardingMessage";

    @Before
    public void setUp() {
        mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        concept = ConceptHelper.buildConcept(SMS_WELCOME_MSG_CONCEPT_NAME);
    }

    @Test
    public void shouldReturnEnglishMsg() {
        String message = concept.getDescriptions().toArray()[0].toString();
        when(cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "eng"))
                .thenReturn(message);
        String actual = cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "eng");
        Assert.assertEquals("Hello patient", actual);
    }

    @Test
    public void shouldReturnDutchMsg() {
        String message = concept.getDescriptions().toArray()[1].toString();
        when(cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "nld"))
                .thenReturn(message);
        String actual = cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "nld");
        Assert.assertEquals("Hallo patiënt", actual);
    }

    @Test
    public void shouldReturnSpanishMsg() {
        String message = concept.getDescriptions().toArray()[2].toString();
        when(cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "spa"))
                .thenReturn(message);
        String actual = cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "spa");
        Assert.assertEquals("Hola paciente", actual);
    }

    @Test
    public void shouldReturnNullIfConceptIsNull() {
        concept = null;
        when(cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "spa"))
                .thenReturn(null);
        String actual = cflConceptService.getMessageByConceptNameAndLanguage(SMS_WELCOME_MSG_CONCEPT_NAME, "spa");
        Assert.assertNull(actual);
    }
}
