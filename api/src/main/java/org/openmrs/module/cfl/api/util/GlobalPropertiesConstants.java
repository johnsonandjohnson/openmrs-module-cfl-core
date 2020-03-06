package org.openmrs.module.cfl.api.util;

public final class GlobalPropertiesConstants {

    public static final String PATIENT_UUID_PARAM = "patientId";
    public static final String VISIT_UUID_PARAM = "visitId";
    public static final String ENCOUNTER_UUID_PARAM = "encounterId";
    public static final String MEDICINE_REFILL_VISIT_TYPE_NAME = "Medicine refill";
    public static final String SPUTUM_COLLECTION_VISIT_TYPE_NAME = "Sputum collection";
    public static final String CREATE_URI_NAME = "create";
    public static final String EDIT_URI_NAME = "edit";
    public static final String DEFAULT_VISIT_FORM_URIS_KEY = "default";

    private static final String MEDICINE_REFILL_CREATE_VISIT_FORM_URI = String.format(
            "/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{%s}}&visitId={{%s}}"
                    + "&definitionUiResource=cfl:htmlforms/cfl-medicine-refill.xml",
            PATIENT_UUID_PARAM, VISIT_UUID_PARAM);

    private static final String SPUTUM_COLLECTION_CREATE_VISIT_FORM_URI = String.format(
            "/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{%s}}&visitId={{%s}}"
                    + "&definitionUiResource=cfl:htmlforms/cfl-sputum-visit-note.xml",
            PATIENT_UUID_PARAM, VISIT_UUID_PARAM);

    private static final String DEFAULT_CREATE_VISIT_FORM_URI = String.format(
            "/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{%s}}&visitId={{%s}}"
                    + "&definitionUiResource=cfl:htmlforms/cfl-visit-note.xml",
            PATIENT_UUID_PARAM, VISIT_UUID_PARAM);

    private static final String DEFAULT_EDIT_VISIT_FORM_URI = String.format(
            "/htmlformentryui/htmlform/editHtmlFormWithStandardUi.page?patientId={{%s}}&encounterId={{%s}}",
            PATIENT_UUID_PARAM, ENCOUNTER_UUID_PARAM);

    public static final GPDefinition VISIT_FORM_URIS = new GPDefinition(
            "visits.visit-form-uris",
            String.format(
                    "{'%s':{'%s':'%s'},"
                            + "'%s':{'%s':'%s'},"
                            + "'%s':{"
                            + "'%s':'%s',"
                            + "'%s':'%s'}}",
                    MEDICINE_REFILL_VISIT_TYPE_NAME,
                    CREATE_URI_NAME, MEDICINE_REFILL_CREATE_VISIT_FORM_URI,
                    SPUTUM_COLLECTION_VISIT_TYPE_NAME,
                    CREATE_URI_NAME, SPUTUM_COLLECTION_CREATE_VISIT_FORM_URI,
                    DEFAULT_VISIT_FORM_URIS_KEY,
                    CREATE_URI_NAME, DEFAULT_CREATE_VISIT_FORM_URI,
                    EDIT_URI_NAME, DEFAULT_EDIT_VISIT_FORM_URI),
            String.format(
                    "The URI templates which leads to current visit form. The value of this property is a JSON "
                            + "map. The map allows to specify URIs based on the visit type. The key in the map could be "
                            + "visitTypeUuid or visitTypeName or 'default'. The value is a nested JSON map which could "
                            + "consists of 2 entries - 'create' and 'edit' URI templates. The template could consists of "
                            + "{{%s}},{{%s}} and {{%s}} variables which will be replaced if URLs are used. \n "
                            + "Note: \n A) if invalid URI is set, no form will be used\n "
                            + "B) if URI is no specified, the form from 'default' settings will be used\n "
                            + "C) if specific and default URIs are not defined, no form will be used\n",
                    PATIENT_UUID_PARAM, ENCOUNTER_UUID_PARAM, VISIT_UUID_PARAM),
            true);

    private GlobalPropertiesConstants() {
    }
}
