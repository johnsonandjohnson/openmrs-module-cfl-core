package org.openmrs.module.cfl.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.web.model.CountryControllerModel;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class CountryControllerTest {

    private static final String MODEL_NAME = "model";

    @InjectMocks
    private CountryController countryController;

    @Mock
    private ConceptService conceptService;

    @Before
    public void setUp() {
        mockStatic(Context.class);
    }

    @Test
    public void getCountryList_shouldCallServiceMethodsSuccessfullyAndReturnNotNullConceptMapModel() {
        when(conceptService.getConceptByName(anyString())).thenReturn(new Concept());
        when(conceptService.getConceptsByConceptSet(new Concept())).thenReturn(anyListOf(Concept.class));

        ModelAndView result = countryController.getCountryList();

        verify(conceptService).getConceptByName(anyString());
        verify(conceptService).getConceptsByConceptSet(any(Concept.class));
        assertNotNull(((CountryControllerModel) result.getModel().get(MODEL_NAME)).getConceptMap());
    }

    @Test
    public void getCountryForm_shouldReturnNullConceptMapModel() {
        ModelAndView result = countryController.getCountryForm();

        assertNull(((CountryControllerModel) result.getModel().get(MODEL_NAME)).getConceptMap());
    }
}
