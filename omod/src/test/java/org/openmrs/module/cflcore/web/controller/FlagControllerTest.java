package org.openmrs.module.cflcore.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.module.cflcore.api.dto.FlagDTO;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.test.BaseContextMockTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FlagControllerTest extends BaseContextMockTest {
  @Mock private FlagService flagService;

  @Before
  public void setup() {
    contextMockHelper.setService(FlagService.class, flagService);
  }

  @Test
  public void shouldReturnAllNotRetiredAndEnabledFlags() {
    final Flag flag = new Flag();
    final Flag disabledFlag = new Flag();
    disabledFlag.setEnabled(Boolean.FALSE);
    final Flag retiredFlag = new Flag();
    retiredFlag.setRetired(Boolean.TRUE);

    when(flagService.getAllFlags()).thenReturn(Arrays.asList(flag, disabledFlag, retiredFlag));

    final FlagController controller = new FlagController();
    final List<FlagDTO> dtos = controller.getAllFlags().getBody();

    assertEquals(1, dtos.size());
    assertEquals(flag.getUuid(), dtos.get(0).getUuid());
  }
}
