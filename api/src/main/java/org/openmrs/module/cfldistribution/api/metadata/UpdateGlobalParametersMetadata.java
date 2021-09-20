package org.openmrs.module.cfldistribution.api.metadata;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.util.GlobalPropertiesConstants;
import org.openmrs.module.metadatadeploy.bundle.VersionedMetadataBundle;

/**
 * Updates Global Parameter values, it's responsible for updating Global Properties to CFL distribution defaults.
 * <p>
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 * </p>
 */
public class UpdateGlobalParametersMetadata extends VersionedMetadataBundle {
    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installEveryTime() throws Exception {
        // nothing to do
    }

    @Override
    protected void installNewVersion() throws Exception {
        final AdministrationService administrationService = Context.getAdministrationService();
        // Enable and keep enabled patient dashboard redirection
        administrationService.setGlobalProperty(CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME,
                Boolean.TRUE.toString());
        administrationService.setGlobalProperty(CFLConstants.CFL_LOGIN_REDIRECT_GLOBAL_PROPERTY_NAME,
                Boolean.TRUE.toString());
        administrationService.setGlobalProperty(GlobalPropertiesConstants.VISIT_FORM_URIS.getKey(), "{\n" + //
                "  \"Medicine refill\": {\n" + //
                "    \"create\": \"/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patientId}}" + //
                "&visitId={{visitId}}&definitionUiResource=cfldistribution:htmlforms/cfl-medicine-refill.xml\"\n" + //
                "  },\n" + //
                "  \"Sputum collection\": {\n" + //
                "    \"create\": \"/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patientId}}" + //
                "&visitId={{visitId}}&definitionUiResource=cfldistribution:htmlforms/cfl-sputum-visit-note.xml\"\n" + //
                "  },\n" +  //
                "  \"default\": {\n" + //
                "    \"create\": \"/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patientId}}" + //
                "&visitId={{visitId}}&definitionUiResource=cfldistribution:htmlforms/cfl-visit-note.xml\",\n" + //
                "    \"edit\": \"/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{patientId}}" + //
                "&encounterId={{encounterId}}\"\n" + //
                "  }\n" + //
                "}");
    }
}
