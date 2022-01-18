package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.Patient;
import org.openmrs.test.BaseContextMockTest;


public class IrisPatientServiceImplTest extends BaseContextMockTest {

    @Captor
    private ArgumentCaptor<Object> captor;

    @Mock
    private Log log;

    @Test
    public void shouldPrintCorrectLogInConsole(){
        //given
        final Patient patient = createTestPatient();
        final IrisPatientServiceImpl irisPatientService = new IrisPatientServiceImpl(log);

        //when
        irisPatientService.updatePatient(patient);

        //then
        Mockito.verify(log).info(captor.capture());
        final String value = (String) captor.getValue();

        Assert.assertEquals("Patient with uuid: " + patient.getUuid() + " has not been updated. Update feature is not supported yet.", value);
    }

    private Patient createTestPatient() {
        final Patient patient = new Patient();
        patient.setUuid("266063d4-7a5b-486a-84cd-0ef002d9d802");
        return patient;
    }
}
