package org.openmrs.module.cfl.web.resource;

import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_12.DrugResource1_12;

/**
 * The CflDrugResource Class.
 *
 * <p>The CfL custom Drug Resource which adds an 'abbreviation' property to the Drug's full
 * representation. The 'abbreviation' is a Short Name of the Drug's Concept or null if there is no
 * Concept or the Concept has no Short Name.
 */
@Resource(
    name = RestConstants.VERSION_1 + "/drug",
    order = CflDrugResource.RESOURCE_ORDER,
    supportedClass = Drug.class,
    supportedOpenmrsVersions = {"1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"})
public class CflDrugResource extends DrugResource1_12 {
  /** The DrugResource1_12 order is 1, we need lower. */
  public static final int RESOURCE_ORDER = 0;

  private static final String ABBREVIATION_PROP = "abbreviation";

  @PropertyGetter(ABBREVIATION_PROP)
  public String getAbbreviation(Drug drug) {
    if (drug.getConcept() == null) {
      return null;
    }

    return drug.getConcept().getNames().stream()
        .filter(name -> name.getConceptNameType() == ConceptNameType.SHORT)
        .findFirst()
        .map(ConceptName::getName)
        .orElse(null);
  }

  @Override
  public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
    final DelegatingResourceDescription representation = super.getRepresentationDescription(rep);

    if (rep instanceof FullRepresentation) {
      representation.addProperty(ABBREVIATION_PROP);
    }

    return representation;
  }
}
