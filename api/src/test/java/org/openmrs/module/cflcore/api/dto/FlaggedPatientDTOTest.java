package org.openmrs.module.cflcore.api.dto;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.cflcore.api.constant.CFLCoreConstants;

public class FlaggedPatientDTOTest {

  @Test
  public void shouldInitializeAllFields() {
    final String patientIdentifier = "patientIdentifier";
    final String patientName = "patientName";
    final String phoneNumber = "phoneNumber";
    final String patientStatus = "patientStatus";
    final String patientUuid = "patientUuid";
    final String genderShort = CFLCoreConstants.MALE_GENDER_SHORT_NAME;
    final Object[] dbRow = {
      patientIdentifier, patientName, phoneNumber, patientStatus, patientUuid, genderShort
    };

    final FlaggedPatientDTO flaggedPatientDTO = new FlaggedPatientDTO(dbRow);

    Assert.assertEquals(patientIdentifier, flaggedPatientDTO.getPatientIdentifier());
    Assert.assertEquals(patientName, flaggedPatientDTO.getPatientName());
    Assert.assertEquals(phoneNumber, flaggedPatientDTO.getPhoneNumber());
    Assert.assertEquals(patientStatus, flaggedPatientDTO.getPatientStatus());
    Assert.assertEquals(patientUuid, flaggedPatientDTO.getPatientUuid());
    Assert.assertEquals(CFLCoreConstants.MALE_GENDER_FULL_NAME, flaggedPatientDTO.getGender());
  }
}
