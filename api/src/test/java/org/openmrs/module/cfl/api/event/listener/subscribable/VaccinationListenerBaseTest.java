package org.openmrs.module.cfl.api.event.listener.subscribable;

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
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.Constant;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.helper.LocationHelper;
import org.openmrs.module.cfl.api.helper.PatientHelper;
import org.openmrs.module.cfl.api.helper.PersonHelper;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.service.VaccinationService;
import org.openmrs.module.cfl.api.service.impl.VaccinationServiceImpl;

import javax.jms.MapMessage;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class VaccinationListenerBaseTest {
    @Mock
    protected MapMessage message;

    @Mock
    protected ConfigService configService;

    @Mock
    protected VisitService visitService;

    @Mock
    protected AdministrationService administrationService;

    @Mock
    protected PatientService patientService;

    @Mock
    protected LocationService locationService;

    @InjectMocks
    protected VaccinationServiceImpl vaccinationService;

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

        when(Context.getRegisteredComponent(CFLConstants.CFL_CONFIG_SERVICE_BEAN_NAME, ConfigService.class)).thenReturn(
                configService);
        when(Context.getVisitService()).thenReturn(visitService);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getLocationService()).thenReturn(locationService);
        when(Context.getService(VaccinationService.class)).thenReturn(vaccinationService);
    }

    protected Vaccination[] createVaccination() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(Constant.COVID_VACCINATION_PROGRAM_JSON_FILE);
        Vaccination vaccination = new Gson().fromJson(IOUtils.toString(in), Vaccination.class);
        return new Vaccination[] {vaccination};
    }
}
