package org.openmrs.module.cfl.api.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

@TransactionConfiguration( defaultRollback = false)
public class IrisPatientServiceImplITTest extends BaseModuleContextSensitiveTest {

    private static final String XML_DATA_SET_PATH = "datasets/";
    private static final String XML_PATIENT_DATASET = "PatientDataset.xml";

    @Autowired
    private IrisPatientServiceImplITTestHelper irisPatientServiceImplITTestHelper;

    @Before
    public void setUp() throws Exception {
        executeDataSet(XML_DATA_SET_PATH + XML_PATIENT_DATASET);
    }

    @Test
    public void shouldSaveNewPatient() {
        irisPatientServiceImplITTestHelper.saveNewPatient();
    }

    @Test
    public void shouldUpdateAlreadyExistingPatient() {
        irisPatientServiceImplITTestHelper.updateAlreadyExistingPatient();
    }
}
