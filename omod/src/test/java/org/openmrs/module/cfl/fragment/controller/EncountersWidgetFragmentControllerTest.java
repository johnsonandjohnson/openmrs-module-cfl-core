package org.openmrs.module.cfl.fragment.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.openmrs.module.cfl.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController.ENCOUNTERS_ATTR_NAME;
import static org.openmrs.module.cfl.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController.ENCOUNTERS_BOOL_LIST_ATTR_NAME;
import static org.openmrs.module.cfl.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController.MAX_RECORDS_ATTR_NAME;
import static org.openmrs.module.cfl.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController.PATIENT_ID;
import static org.openmrs.module.cfl.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController.TEXTS_LIST_ATTR_NAME;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class EncountersWidgetFragmentControllerTest {

    private static final String ENCOUNTER_TYPE_UUID_ATTR_NAME = "encounterTypeUuid";

    private static final String ENCOUNTER_TYPE_UUID = "43c3630f-abfe-4fe1-8c92-b73b65199a3d";

    private static final String MAX_RECORD_SIZE_ATTR_NAME = "maxRecords";

    private static final String MAX_RECORD_SIZE = "20";

    private EncountersWidgetFragmentController controller = new EncountersWidgetFragmentController();

    private FragmentConfiguration fragmentConfiguration;

    @Mock
    private FragmentModel fragmentModel;

    @Mock
    private Patient patient;

    @Mock
    private EncounterService encounterService;

    @Mock
    private ConceptService conceptService;

    @Test
    public void shouldProperlyAddAttributes() {
        fragmentConfiguration = createFragmentConfiguration(MAX_RECORD_SIZE);

        controller.controller(fragmentConfiguration, fragmentModel, patient, encounterService, conceptService);

        verify(fragmentModel).addAttribute(PATIENT_ID, patient.getUuid());
        verify(fragmentModel).addAttribute(ENCOUNTERS_ATTR_NAME, new ArrayList<Encounter>());
        verify(fragmentModel).addAttribute(MAX_RECORDS_ATTR_NAME, 20);
        verify(fragmentModel).addAttribute(TEXTS_LIST_ATTR_NAME, new ArrayList<String>());
        verify(fragmentModel).addAttribute(ENCOUNTERS_BOOL_LIST_ATTR_NAME, new ArrayList<Boolean>());
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowExceptionIfMaxRecordSizeIsIntValue() {
        fragmentConfiguration = createFragmentConfiguration(Integer.parseInt(MAX_RECORD_SIZE));


        controller.controller(fragmentConfiguration, fragmentModel, patient, encounterService, conceptService);

        verify(fragmentModel).addAttribute(PATIENT_ID, patient.getUuid());
        verify(fragmentModel).addAttribute(ENCOUNTERS_ATTR_NAME, new ArrayList<Encounter>());
        verify(fragmentModel).addAttribute(MAX_RECORDS_ATTR_NAME, 20);
        verify(fragmentModel).addAttribute(TEXTS_LIST_ATTR_NAME, new ArrayList<String>());
        verify(fragmentModel).addAttribute(ENCOUNTERS_BOOL_LIST_ATTR_NAME, new ArrayList<Boolean>());
    }

    private FragmentConfiguration createFragmentConfiguration(Object maxRecords) {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ENCOUNTER_TYPE_UUID_ATTR_NAME, ENCOUNTER_TYPE_UUID);
        config.put(MAX_RECORD_SIZE_ATTR_NAME, maxRecords);
        return new FragmentConfiguration(config);
    }
}
