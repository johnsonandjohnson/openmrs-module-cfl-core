/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.web.resource;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.LocationResource2_0;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * The CfLLocationResource Class.
 *
 * <p>This overrides default OpenMRS REST resource for Location and provides workaround fix for
 * handling Location Attributes in more FE-friendly way.
 */
@Resource(
    order = CfLLocationResource.RESOURCE_ORDER,
    name = RestConstants.VERSION_1 + "/location",
    supportedClass = Location.class,
    supportedOpenmrsVersions = {"2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"})
public class CfLLocationResource extends LocationResource2_0 {
  public static final int RESOURCE_ORDER = 10;

  private static final String ATTRIBUTES_REPRESENTATION_PROPERTY = "attributes";
  private static final String ACTIVE_ATTRIBUTES_ENTITY_PROPERTY = "activeAttributes";

  @PropertySetter(ATTRIBUTES_REPRESENTATION_PROPERTY)
  public static void setAttributes(Location instance, List<LocationAttribute> attrs) {
    voidOther(instance, attrs);
    attrs.forEach(instance::setAttribute);
  }

  @Override
  public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
    final DelegatingResourceDescription description = super.getRepresentationDescription(rep);

    if (description == null) {
      return null;
    }

    ofNullable(description.getProperties().get(ATTRIBUTES_REPRESENTATION_PROPERTY))
        .ifPresent(
            attributeProp -> attributeProp.setDelegateProperty(ACTIVE_ATTRIBUTES_ENTITY_PROPERTY));
    return description;
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
