package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.util.GlobalPropertiesConstants;
import org.openmrs.module.cfldistribution.CfldistributionGlobalParameterConstants;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

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
    return 3;
  }

  @Override
  protected void installEveryTime() throws Exception {
    // nothing to do
  }

  @Override
  protected void installNewVersion() throws Exception {
    updateGlobalProperties();
  }

  private void updateGlobalProperties() {
    // Enable and keep enabled patient dashboard redirection
    updateGlobalPropertyIfExists(
        CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME, Boolean.TRUE.toString());
    updateGlobalPropertyIfExists(
        CFLConstants.CFL_LOGIN_REDIRECT_GLOBAL_PROPERTY_NAME, Boolean.TRUE.toString());
    updateGlobalPropertyIfExists(
        GlobalPropertiesConstants.VISIT_FORM_URIS.getKey(),
        "{\n"
            + //
            "  \"Medicine refill\": {\n"
            + //
            "    \"create\": \"/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patientId}}"
            + //
            "&visitId={{visitId}}&definitionUiResource=cfldistribution:htmlforms/cfl-medicine-refill.xml\"\n"
            + //
            "  },\n"
            + //
            "  \"Sputum collection\": {\n"
            + //
            "    \"create\": \"/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patientId}}"
            + //
            "&visitId={{visitId}}&definitionUiResource=cfldistribution:htmlforms/cfl-sputum-visit-note.xml\"\n"
            + //
            "  },\n"
            + //
            "  \"default\": {\n"
            + //
            "    \"create\": \"/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patientId}}"
            + //
            "&visitId={{visitId}}&definitionUiResource=cfldistribution:htmlforms/cfl-visit-note.xml\",\n"
            + //
            "    \"edit\": \"/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{patientId}}"
            + //
            "&encounterId={{encounterId}}\"\n"
            + //
            "  }\n"
            + //
            "}");
    updateGlobalPropertyIfExists(
        GlobalPropertiesConstants.TELEPHONE_NUMBER_PERSON_ATTRIBUTE_TYPE_UUID.getKey(),
        CfldistributionGlobalParameterConstants.CFL_TELEPHONE_NUMBER_PERSON_ATTRIBUTE_TYPE_UUID);
    updateGlobalPropertyIfExists(
        GlobalPropertiesConstants.EMAIL_ADDRESS_PERSON_ATTRIBUTE_TYPE_UUID.getKey(),
        CfldistributionGlobalParameterConstants.CFL_EMAIL_ADDRESS_PERSON_ATTRIBUTE_TYPE_UUID);
  }

  private void updateGlobalPropertyIfExists(String property, String value) {
    final AdministrationService administrationService = Context.getAdministrationService();
    GlobalProperty gp = administrationService.getGlobalPropertyObject(property);
    if (gp != null) {
      administrationService.setGlobalProperty(property, value);
    }
  }
}
