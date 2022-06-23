/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.fragment.controller.dashboardwidgets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.openmrs.module.cflcore.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController.ENCOUNTERS_ATTR_NAME;
import static org.openmrs.module.cflcore.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController.ENCOUNTERS_BOOL_LIST_ATTR_NAME;
import static org.openmrs.module.cflcore.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController.MAX_RECORDS_ATTR_NAME;
import static org.openmrs.module.cflcore.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController.PATIENT_ID;
import static org.openmrs.module.cflcore.fragment.controller.dashboardwidgets.EncountersWidgetFragmentController.TEXTS_LIST_ATTR_NAME;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class EncountersWidgetFragmentControllerTest {

    private static final String ENCOUNTER_TYPE_UUID_ATTR_NAME = "encounterTypeUuid";

    private static final String ENCOUNTER_TYPE_UUID = "43c3630f-abfe-4fe1-8c92-b73b65199a3d";

    private static final String ENCOUNTER_CLOSED_STATUS_CONCEPT_UUID_ATTR_NAME = "encounterClosedStatusConceptUuid";

    private static final String ENCOUNTER_CLOSED_STATUS_CONCEPT_UUID = "a3ac4721-1035-4480-8fc0-f0407a7ff3f1";

    private static final String ANSWER_CONCEPT_UUID_ATTR_NAME = "answerConceptUuid";

    private static final String ANSWER_CONCEPT_UUID = "8a9eefa0-6d4f-4a35-ad00-a999846d8f48";

    private static final String OBS_ANSWER_CONCEPT_CLASS_UUIDS_PARAM_NAME_ATTR_NAME = "obsAnswerConceptsClassUuids";

    private static final String OBS_ANSWER_CONCEPTS_CLASS_UUID = "8d4918b0-c2cc-11de-8d13-0010c6dffd0f";

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
        Map<String, Object> config = new HashMap<>();
        config.put(ENCOUNTER_TYPE_UUID_ATTR_NAME, ENCOUNTER_TYPE_UUID);
        config.put(ENCOUNTER_CLOSED_STATUS_CONCEPT_UUID_ATTR_NAME, ENCOUNTER_CLOSED_STATUS_CONCEPT_UUID);
        config.put(ANSWER_CONCEPT_UUID_ATTR_NAME, ANSWER_CONCEPT_UUID);
        config.put(OBS_ANSWER_CONCEPT_CLASS_UUIDS_PARAM_NAME_ATTR_NAME, OBS_ANSWER_CONCEPTS_CLASS_UUID);
        config.put(MAX_RECORD_SIZE_ATTR_NAME, maxRecords);
        return new FragmentConfiguration(config);
    }
}
