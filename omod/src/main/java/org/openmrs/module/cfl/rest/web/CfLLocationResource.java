package org.openmrs.module.cfl.rest.web;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.LocationResource2_0;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The CfLLocationResource Class.
 * <p>
 *    This overrides default OpenMRS REST resource for Location and provides workaround fix for handling Location
 *    Attributes in more FE-friendly way.
 * </p>
 */
@Resource(
    order = CfLLocationResource.RESOURCE_ORDER,
    name = RestConstants.VERSION_1 + "/location",
    supportedClass = Location.class,
    supportedOpenmrsVersions = {"2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"})
public class CfLLocationResource extends LocationResource2_0 {
  public static final int RESOURCE_ORDER = 10;

  @PropertySetter("attributes")
  public static void setAttributes(Location instance, List<LocationAttribute> attrs) {
    voidOther(instance, attrs);

    for (LocationAttribute attribute : attrs) {
      instance.setAttribute(attribute);
    }
  }

  private static void voidOther(Location instance, List<LocationAttribute> attrs) {
    final Set<String> newAttributeTypeUuids =
        attrs.stream()
            .map(BaseAttribute::getAttributeType)
            .map(BaseOpenmrsObject::getUuid)
            .collect(Collectors.toSet());

    instance.getActiveAttributes().stream()
        .filter(attr -> !newAttributeTypeUuids.contains(attr.getAttributeType().getUuid()))
        .forEach(attr -> attr.setVoided(true));
  }
}
