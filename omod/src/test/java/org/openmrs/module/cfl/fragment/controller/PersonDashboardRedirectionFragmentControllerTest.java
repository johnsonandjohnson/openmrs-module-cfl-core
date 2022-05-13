/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.fragment.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.RelationshipType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.openmrs.module.cfl.fragment.controller.PersonDashboardRedirectionFragmentController.ACTOR_TYPE_NAME;
import static org.openmrs.module.cfl.fragment.controller.PersonDashboardRedirectionFragmentController.ACTOR_UUID;
import static org.openmrs.module.cfl.fragment.controller.PersonDashboardRedirectionFragmentController.A_POSITION;
import static org.openmrs.module.cfl.fragment.controller.PersonDashboardRedirectionFragmentController.IS_PATIENT_DASHBOARD;
import static org.openmrs.module.cfl.fragment.controller.PersonDashboardRedirectionFragmentController.SHOW_INFORMATION_ABOUT_ACTOR_DASHBOARD;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class})
public class PersonDashboardRedirectionFragmentControllerTest {

    private PersonDashboardRedirectionFragmentController controller = new PersonDashboardRedirectionFragmentController();

    private static final String PATIENT_UUID = "99abe053-3295-49bc-b469-cdfa5c7236d7";
    private static final String RELATIONSHIP_TYPE_UUID = "acec590b-825e-45d2-876a-0028f174903d";
    private static final String CAREGIVER = "Caregiver";

    @Mock
    private PersonService personService;

    @Mock
    private FragmentModel fragmentModel;

    @Mock
    private RelationshipType relationshipType;

    @Mock
    private AdministrationService administrationService;

    @Before
    public void setUp() {
        mockStatic(Context.class);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(administrationService.getGlobalProperty(CFLConstants.SUPPORTED_ACTOR_TYPE_DIRECTION)).thenReturn(A_POSITION);
        when(personService.getRelationshipTypeByUuid(RELATIONSHIP_TYPE_UUID)).thenReturn(relationshipType);
        when(relationshipType.getaIsToB()).thenReturn(CAREGIVER);
    }

    @Test
    public void shouldProperlyAddAttributes() {
        when(administrationService.getGlobalProperty(CFLConstants.SUPPORTED_ACTOR_TYPE)).thenReturn(RELATIONSHIP_TYPE_UUID);

        controller.controller(personService, true, PATIENT_UUID, fragmentModel);

        verify(fragmentModel).addAttribute(ACTOR_UUID, PATIENT_UUID);
        verify(fragmentModel).addAttribute(IS_PATIENT_DASHBOARD, true);
        verify(fragmentModel).addAttribute(SHOW_INFORMATION_ABOUT_ACTOR_DASHBOARD, false);
        verify(fragmentModel).addAttribute(ACTOR_TYPE_NAME, CAREGIVER);
    }

    @Test
    public void shouldProperlyHandleEmptyUuidRelationshipType() {
        when(administrationService.getGlobalProperty(CFLConstants.SUPPORTED_ACTOR_TYPE)).thenReturn(null);

        controller.controller(personService, true, PATIENT_UUID, fragmentModel);

        verify(fragmentModel).addAttribute(ACTOR_UUID, PATIENT_UUID);
        verify(fragmentModel).addAttribute(IS_PATIENT_DASHBOARD, true);
        verify(fragmentModel).addAttribute(SHOW_INFORMATION_ABOUT_ACTOR_DASHBOARD, false);
        verify(fragmentModel).addAttribute(ACTOR_TYPE_NAME, null);
    }
}
