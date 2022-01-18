package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationTag;

public class LocationMetadata extends VersionedMetadataBundle {
  private static final String LOCATION_UUID_PROPERTY_NAME = "locationUuid";
  private static final String UNKNOWN_LOCATION_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
  private static final String CFL_CLINIC_LOCATION_NAME = "CFL Clinic";
  private static final String LOGIN_LOCATION_TAG_NAME = "Login Location";
  private static final String VISIT_LOCATION_TAG_NAME = "Visit Location";
  private static final String VISIT_LOCATION_UUID = "37dd4458-dc9e-4ae6-a1f1-789c1162d37b";

  @Override
  public int getVersion() {
    return 1;
  }

  @Override
  protected void installEveryTime() {}

  @Override
  protected void installNewVersion() {
    install(locationTag(VISIT_LOCATION_TAG_NAME, "", VISIT_LOCATION_UUID));
    updateUserProperty(
        CFLConstants.ADMIN_USER_NAME, LOCATION_UUID_PROPERTY_NAME, UNKNOWN_LOCATION_UUID);
    updateLocationName(UNKNOWN_LOCATION_UUID, CFL_CLINIC_LOCATION_NAME);
    addNewLocationTagToLocation(UNKNOWN_LOCATION_UUID, LOGIN_LOCATION_TAG_NAME);
    addNewLocationTagToLocation(UNKNOWN_LOCATION_UUID, VISIT_LOCATION_TAG_NAME);
  }

  private void updateUserProperty(String username, String property, String value) {
    final UserService userService = Context.getUserService();
    final User user = userService.getUserByUsername(username);

    if (user != null) {
      user.setUserProperty(property, value);
    } else {
      log.warn(String.format("User with username: %s not found", username));
    }
  }

  private void updateLocationName(String locationUuid, String newName) {
    final Location location = Context.getLocationService().getLocationByUuid(locationUuid);

    if (location != null) {
      location.setName(newName);
    } else {
      log.warn(String.format("Location with uuid: %s not found", locationUuid));
    }
  }

  private void addNewLocationTagToLocation(String locationUuid, String locationTagName) {
    LocationService locationService = Context.getLocationService();
    Location location = locationService.getLocationByUuid(locationUuid);
    if (location != null) {
      location.addTag(locationService.getLocationTagByName(locationTagName));
    } else {
      log.warn(String.format("Location with uuid: %s not found", locationUuid));
    }
  }
}
