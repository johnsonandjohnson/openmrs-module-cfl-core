package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.service.ConfigService;
import org.openmrs.module.cfl.api.util.GlobalPropertiesConstants;
import org.openmrs.module.cfldistribution.api.activator.ModuleActivatorStep;

import static org.openmrs.module.cfldistribution.api.activator.impl.ModuleActivatorStepOrderEnum.UPDATE_GLOBAL_PARAMETERS_ACTIVATOR_STEP;

/**
 * Updates Global Parameter values. The step is responsible for updating Global Properties to CFL distribution defaults,
 * from CFL module defaults.
 * <p>
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 * </p>
 */
public class UpdateGlobalParametersActivatorStep implements ModuleActivatorStep {
    @Override
    public int getOrder() {
        return UPDATE_GLOBAL_PARAMETERS_ACTIVATOR_STEP.ordinal();
    }

    @Override
    public void startup(Log log) throws Exception {
        final AdministrationService administrationService = Context.getAdministrationService();
        // Enable and keep enabled patient dashboard redirection
        administrationService.setGlobalProperty(CFLConstants.PATIENT_DASHBOARD_REDIRECT_GLOBAL_PROPERTY_NAME,
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
