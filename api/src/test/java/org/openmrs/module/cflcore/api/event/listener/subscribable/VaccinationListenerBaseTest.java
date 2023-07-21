/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.event.listener.subscribable;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cflcore.CFLConstants;
import org.openmrs.module.cflcore.Constant;
import org.openmrs.module.cflcore.api.contract.Vaccination;
import org.openmrs.module.cflcore.api.helper.LocationHelper;
import org.openmrs.module.cflcore.api.helper.PatientHelper;
import org.openmrs.module.cflcore.api.helper.PersonHelper;
import org.openmrs.module.cflcore.api.service.ConfigService;
import org.openmrs.module.cflcore.api.service.PatientVisitConfigService;import org.openmrs.module.cflcore.api.service.VaccinationService;
import org.openmrs.module.cflcore.api.service.impl.VaccinationServiceImpl;

import javax.jms.MapMessage;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class VaccinationListenerBaseTest {
  @Mock protected MapMessage message;

  @Mock protected ConfigService configService;

  @Mock protected VisitService visitService;

  @Mock protected AdministrationService administrationService;

  @Mock protected PatientService patientService;

  @Mock protected LocationService locationService;

  @Mock protected org.openmrs.module.visits.api.service.ConfigService visitsConfigService;

  @Mock
  protected PatientVisitConfigService patientVisitConfigService;

  @InjectMocks protected VaccinationServiceImpl vaccinationService;

  protected Person person;
  protected Patient patient;
  protected Location location;
  protected Visit visit;
  protected Vaccination[] vaccinations;

  @Before
  public void setUp() throws IOException {
    mockStatic(Context.class);

    person = PersonHelper.createPerson();
    location = LocationHelper.createLocation();
    patient = PatientHelper.createPatient(person, location);
    vaccinations = createVaccination();

    when(Context.getService(ConfigService.class)).thenReturn(configService);
    when(Context.getVisitService()).thenReturn(visitService);
    when(Context.getAdministrationService()).thenReturn(administrationService);
    when(Context.getPatientService()).thenReturn(patientService);
    when(Context.getLocationService()).thenReturn(locationService);
    when(Context.getService(VaccinationService.class)).thenReturn(vaccinationService);
    when(Context.getService(org.openmrs.module.visits.api.service.ConfigService.class))
        .thenReturn(visitsConfigService);
    when(Context.getService(PatientVisitConfigService.class)).thenReturn(patientVisitConfigService);
  }

  protected Vaccination[] createVaccination() throws IOException {
    InputStream in =
        this.getClass()
            .getClassLoader()
            .getResourceAsStream(Constant.COVID_VACCINATION_PROGRAM_JSON_FILE);
    Vaccination vaccination = new Gson().fromJson(IOUtils.toString(in), Vaccination.class);
    return new Vaccination[] {vaccination};
  }
}
