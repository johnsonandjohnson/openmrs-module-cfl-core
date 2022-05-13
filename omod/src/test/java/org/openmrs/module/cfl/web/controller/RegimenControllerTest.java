/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.dto.RegimensPatientsDataDTO;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class RegimenControllerTest {

    @InjectMocks
    private RegimenController regimenController;

    @Mock
    private AdministrationService administrationService;

    @Mock
    private VaccinationService vaccinationService;

    @Before
    public void setUp() {
        mockStatic(Context.class);

        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getRegisteredComponent(CFLConstants.VACCINATION_SERVICE_BEAN_NAME, VaccinationService.class))
                .thenReturn(vaccinationService);
    }

    @Test
    public void getRegimensPatientsInfo_shouldCallMethodSuccessfullyAndReturnStatusOK() {
        ResponseEntity<List<RegimensPatientsDataDTO>> response = regimenController.getRegimensPatientsInfo();

        verify(administrationService, times(1))
                .getGlobalProperty(CFLConstants.MAIN_CONFIG);
        verify(vaccinationService, times(1)).getRegimenResultsList(anyString());
        assertEquals(200, response.getStatusCode().value());
    }
}
