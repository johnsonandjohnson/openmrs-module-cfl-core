package org.openmrs.module.cfldistribution.appframework;

import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Location;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertTrue;

public class LocationBasedLoginLocationFilterCompatibility_0_2Test
    extends BaseModuleContextSensitiveTest {

  @Test
  public void shouldAcceptSuperUser() throws Exception {
    final Location locationMock = Mockito.mock(Location.class);
    final LocationBasedLoginLocationFilterCompatibility_0_2 compatibility =
        new LocationBasedLoginLocationFilterCompatibility_0_2();

    this.authenticate();

    assertTrue(compatibility.accept(locationMock));
  }
}
