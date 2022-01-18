package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.util.GlobalPropertiesConstants;
import org.openmrs.module.cfldistribution.CfldistributionGlobalParameterConstants;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;
import org.openmrs.util.OpenmrsConstants;

/**
 * Updates Global Parameter values, it's responsible for updating Global Properties to CFL
 * distribution defaults.
 *
 * <p>The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with
 * annotated beans.
 */
public class UpdateGlobalParametersMetadata extends VersionedMetadataBundle {
  @Override
  public int getVersion() {
    return 4;
  }

  @Override
  protected void installEveryTime() {
    // nothing to do
  }

  @Override
  protected void installNewVersion() {
    updateGlobalProperties();
  }

  private void updateGlobalProperties() {
    updateCFLProperties();
    updateOrderEntryProperties();
  }

  private void updateCFLProperties() {
    // Enable and keep enabled patient dashboard redirection
    updateGlobalPropertyIfExists(
        CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME, Boolean.TRUE.toString());
    updateGlobalPropertyIfExists(
        GlobalPropertiesConstants.TELEPHONE_NUMBER_PERSON_ATTRIBUTE_TYPE_UUID.getKey(),
        CfldistributionGlobalParameterConstants.CFL_TELEPHONE_NUMBER_PERSON_ATTRIBUTE_TYPE_UUID);
    updateGlobalPropertyIfExists(
        GlobalPropertiesConstants.EMAIL_ADDRESS_PERSON_ATTRIBUTE_TYPE_UUID.getKey(),
        CfldistributionGlobalParameterConstants.CFL_EMAIL_ADDRESS_PERSON_ATTRIBUTE_TYPE_UUID);
  }

  private void updateOrderEntryProperties() {
    // Updates to CIEL Concepts Sets as default values
    updateGlobalPropertyIfExists(
        OpenmrsConstants.GP_DRUG_DISPENSING_UNITS_CONCEPT_UUID,
        "162402AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    updateGlobalPropertyIfExists(
        OpenmrsConstants.GP_DRUG_DOSING_UNITS_CONCEPT_UUID, "162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    updateGlobalPropertyIfExists(
        OpenmrsConstants.GP_DRUG_ROUTES_CONCEPT_UUID, "162394AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    updateGlobalPropertyIfExists(
        OpenmrsConstants.GP_DURATION_UNITS_CONCEPT_UUID, "1732AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
  }

  private void updateGlobalPropertyIfExists(String property, String value) {
    final AdministrationService administrationService = Context.getAdministrationService();
    GlobalProperty gp = administrationService.getGlobalPropertyObject(property);
    if (gp != null) {
      administrationService.setGlobalProperty(property, value);
    }
  }
}
