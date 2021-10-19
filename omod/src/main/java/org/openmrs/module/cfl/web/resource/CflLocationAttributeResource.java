package org.openmrs.module.cfl.web.resource;

import org.apache.commons.lang.StringUtils;
import org.openmrs.LocationAttribute;
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
      instance.setValue(value);
    } else {
      instance.setValue(valueDatatypeHandler.fromReferenceString(value));
    }
  }
}
