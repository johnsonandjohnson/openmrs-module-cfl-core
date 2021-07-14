package org.openmrs.module.cfl.web.controller;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.CFLPatientService;
import org.openmrs.module.cfl.web.dto.RegimensPatientsDataDTO;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

    private static final String CFL_VACCINES = "CFLVaccines.json";

    private static final String VAC_001 = "vac001";

    private static final String VAC_002 = "vac002";

    private static final String VAC_003 = "vac003";

    @InjectMocks
    private RegimenController regimenController;

    @Mock
    private CFLPatientService cflPatientService;

    @Mock
    private AdministrationService administrationService;

    private PersonAttributeType vaccinationProgramAttrType;

    @Before
    public void setUp() {
        mockStatic(Context.class);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getRegisteredComponent(CFLConstants.CFL_PATIENT_SERVICE_BEAN_NAME, CFLPatientService.class))
                .thenReturn(cflPatientService);
        when(cflPatientService.findByVaccinationName(VAC_001)).thenReturn(preparePatientsWithVac001());
        when(cflPatientService.findByVaccinationName(VAC_002)).thenReturn(preparePatientsWithVac002());
        when(cflPatientService.findByVaccinationName(VAC_003)).thenReturn(preparePatientsWithVac003());

        vaccinationProgramAttrType = buildAttributeType(1, "Vaccination program");
    }

    @Test
    public void getRegimensPatientsInfo_whenVaccinesGPIsNotBlank() throws IOException {
        String cflVaccinesGP = jsonToString(CFL_VACCINES);
        when(administrationService.getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY)).thenReturn(cflVaccinesGP);

        ResponseEntity<List<RegimensPatientsDataDTO>> response = regimenController.getRegimensPatientsInfo();

        verify(administrationService, times(1))
                .getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        verify(cflPatientService, times(3)).findByVaccinationName(anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());

        RegimensPatientsDataDTO firstElem = response.getBody().get(0);
        assertEquals(VAC_001, firstElem.getRegimenName());
        assertEquals(3, firstElem.getPatientUuids().size());
        assertEquals((Integer) 3, firstElem.getNumberOfParticipants());
        assertEquals(true, firstElem.isAnyPatientLinkedWithRegimen());

        RegimensPatientsDataDTO lastElem = response.getBody().get(2);
        assertEquals(VAC_003, lastElem.getRegimenName());
        assertEquals(0, lastElem.getPatientUuids().size());
        assertEquals((Integer) 0, lastElem.getNumberOfParticipants());
        assertEquals(false, lastElem.isAnyPatientLinkedWithRegimen());
    }

    @Test
    public void getRegimensPatientsInfo_whenVaccinesGPIsBlank() {
        when(administrationService.getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY)).thenReturn(null);

        ResponseEntity<List<RegimensPatientsDataDTO>> response = regimenController.getRegimensPatientsInfo();

        verify(administrationService, times(1))
                .getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        verify(cflPatientService, times(0)).findByVaccinationName(anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    private List<Patient> preparePatientsWithVac001() {
        Patient patient1 = buildPatientWithAttributes(1, false,
                "f8e41767-e3ee-11eb-8d05-0242ac130001", vaccinationProgramAttrType, VAC_001);
        Patient patient2 = buildPatientWithAttributes(2, false,
                "f8e41767-e3ee-11eb-8d05-0242ac130002", vaccinationProgramAttrType, VAC_001);
        Patient patient3 = buildPatientWithAttributes(3, false,
                "f8e41767-e3ee-11eb-8d05-0242ac130003", vaccinationProgramAttrType, VAC_001);
        return Arrays.asList(patient1, patient2, patient3);
    }

    private List<Patient> preparePatientsWithVac002() {
        Patient patient1 = buildPatientWithAttributes(4, false,
                "f8e41767-e3ee-11eb-8d05-0242ac130004", vaccinationProgramAttrType, VAC_002);
        Patient patient2 = buildPatientWithAttributes(5, false,
                "f8e41767-e3ee-11eb-8d05-0242ac130005", vaccinationProgramAttrType, VAC_002);
        return Arrays.asList(patient1, patient2);
    }

    private List<Patient> preparePatientsWithVac003() {
        return new ArrayList<>();
    }

    private Patient buildPatientWithAttributes(Integer id, boolean isDead, String patientUuid,
                                               PersonAttributeType attributeType, String attributeValue) {
        Person person = buildPerson(id, isDead, patientUuid);
        PersonAttribute personAttribute = buildPersonAttribute(id, person, attributeType, attributeValue);
        person.addAttribute(personAttribute);
        return new Patient(person);
    }

    private PersonAttribute buildPersonAttribute(Integer id, Person person,  PersonAttributeType type, String value) {
        PersonAttribute personAttribute = new PersonAttribute();
        personAttribute.setId(id);
        personAttribute.setPerson(person);
        personAttribute.setAttributeType(type);
        personAttribute.setValue(value);
        return personAttribute;
    }

    private Person buildPerson(Integer id, Boolean dead, String uuid) {
        Person person = new Person();
        person.setId(id);
        person.setDead(dead);
        person.setUuid(uuid);
        return person;
    }

    private PersonAttributeType buildAttributeType(Integer id, String name) {
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setId(id);
        personAttributeType.setName(name);
        return personAttributeType;
    }

    private String jsonToString(String jsonFile) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFile);
        return IOUtils.toString(in);
    }
}
