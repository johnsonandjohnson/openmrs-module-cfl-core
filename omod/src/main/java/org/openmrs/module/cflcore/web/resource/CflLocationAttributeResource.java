/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.web.resource;

import org.apache.commons.lang.StringUtils;
import org.openmrs.LocationAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.attribute.Attribute;
import org.openmrs.attribute.AttributeType;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.customdatatype.datatype.LongFreeTextDatatype;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.LocationAttributeResource1_9;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.LocationResource1_9;

/**
 * The CflLocationAttributeResource Class.
 *
 * <p>This is OpenMRS REST sub-resource for Location's attributes. It contains workaround for {@link
 * LongFreeTextDatatype} handling in via new OpenMRS REST.
 */
@SubResource(
    order = CflLocationAttributeResource.SUB_RESOURCE_ORDER,
    parent = LocationResource1_9.class,
    path = "attribute",
    supportedClass = LocationAttribute.class,
    supportedOpenmrsVersions = {
      "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"
    })
public class CflLocationAttributeResource extends LocationAttributeResource1_9 {
  public static final int SUB_RESOURCE_ORDER = 10;

  @PropertySetter("value")
  public static void setValue(Attribute<?, ?> instance, String value) {
    if (StringUtils.isEmpty(value)) {
      return;
    }

    final AttributeType valueAttributeType = instance.getAttributeType();
    final CustomDatatype<?> valueDatatypeHandler =
        CustomDatatypeUtil.getDatatype(
            valueAttributeType.getDatatypeClassname(), valueAttributeType.getDatatypeConfig());

    if (valueDatatypeHandler instanceof LongFreeTextDatatype) {
      instance.setValue(safeLongFreeTextFromReferenceString(value));
    } else {
      instance.setValue(valueDatatypeHandler.fromReferenceString(value));
    }
  }

  private static String safeLongFreeTextFromReferenceString(String value) {
    final ClobDatatypeStorage clobStorage =
        Context.getDatatypeService().getClobDatatypeStorageByUuid(value);

    return clobStorage == null ? value : clobStorage.getValue();
  }
}
