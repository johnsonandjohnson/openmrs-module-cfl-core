package org.openmrs.module.cfl.api.service;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class SendAdHocMessageITTest extends BaseModuleContextSensitiveTest {

    private static final String XML_DATA_SET_PATH = "datasets/";
    private static final String XML_PERSON_ATTRIBUTE_DATASET = "PersonAttributeDataset.xml";

    @Autowired
    private SendAdHocMessage sendAdHocMessage;

    @Before
    public void setUp() throws Exception {
        executeDataSet(XML_DATA_SET_PATH + XML_PERSON_ATTRIBUTE_DATASET);
    }

    @Test
    public void shouldReturnListOfPatientPhonesAndLanguages() {
        final List<PhoneAndLanguage> result = sendAdHocMessage.getPhonesAndLanguages("1","1000");

        assertThat(result, hasSize(3));
        assertThat(result, hasItems(
                allOf(hasProperty("phoneNumber", is("1111111111")),hasProperty("language", is("en"))),
                allOf(hasProperty("phoneNumber", is("2222222222")),hasProperty("language", is("en"))),
                allOf(hasProperty("phoneNumber", is("3333333333")),hasProperty("language", is("ph")))));
    }
}