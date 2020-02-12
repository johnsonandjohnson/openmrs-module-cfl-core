package org.openmrs.module.cfl.api.util;

import java.util.Arrays;
import java.util.List;

public final class AppFrameworkConstants {

    public static final String CFL_PATIENT_DASHBOARD_IMPROVEMENTS_APP = "cfl.patientDashboard.improvements";

    public static final String CFL_RELATIONSHIPS_APP = "cfl.relationships";

    public static final String CFL_LATESTOBSFORCONCEPTLIS_APP = "cfl.latestobsforconceptlis";

    public static final String APPOINTMENTSCHEDULINGUI_HOME_APP = "appointmentschedulingui.homeApp";

    public static final String SCHEDULING_APPOINTMENT_APP = "appointmentschedulingui.schedulingAppointmentApp";

    public static final String REQUEST_APPOINTMENT_APP = "appointmentschedulingui.requestAppointmentApp";

    public static final String COREAPPS_CONDITIONLIST_APP = "coreapps.conditionlist";

    public static final String COREAPPS_LATEST_OBS_FOR_CONCEPT_LIST_APP = "coreapps.latestObsForConceptList";

    public static final String COREAPPS_OBS_ACROSS_ENCOUNTERS_APP = "coreapps.obsAcrossEncounters";

    public static final String COREAPPS_OBS_GRAPH_APP = "coreapps.obsGraph";

    public static final String COREAPPS_RELATIONSHIPS_APP = "coreapps.relationships";

    public static final String COREAPPS_MOST_RECENT_VITALS_APP = "coreapps.mostRecentVitals";

    public static final String APPOINTMENTSCHEDULINGUI_TAB_EXT = "appointmentschedulingui.tab";

    public static final String PATIENT_DASHBOARD_APPOINTMENTS_EXT = "org.openmrs.module." +
            "appointmentschedulingui.firstColumnFragments.patientDashboard.patientAppointments";

    public static final String STICKY_NOTE_EXT = "org.openmrs.module.coreapps.patientHeader" +
            ".secondLineFragments.stickyNote";

    public static final String COREAPPS_CREATE_VISIT_EXT = "org.openmrs.module.coreapps.createVisit";

    public static final String COREAPPS_CREATE_RETROSPECTIVE_VISIT_EXT = "org.openmrs.module.coreapps." +
            "createRetrospectiveVisit";

    public static final String COREAPPS_MERGE_VISITS_EXT = "org.openmrs.module.coreapps.mergeVisits";

    public static final String ALLERGYUI_PATIENT_DASHBOARD_EXT = "org.openmrs.module.allergyui." +
            "patientDashboard.secondColumnFragments";

    public static final String PATIENTFLAGS_EXT = "patientflags.patientDashboard.secondColumnFragments";

    public static final String ATTACHMENTS_EXT = "org.openmrs.module.attachments.patientDashboard." +
            "secondColumnFragments.att";

    public static final String CHARTSEARCH_EXT = "chartsearch.chartSearchLink";

    public static final String LOCATIONBASEDACCESS_EXT = "org.openmrs.module.locationbasedaccess.editLocation";

    public static final List<String> APP_IDS = Arrays.asList(APPOINTMENTSCHEDULINGUI_HOME_APP,
            SCHEDULING_APPOINTMENT_APP, REQUEST_APPOINTMENT_APP, COREAPPS_CONDITIONLIST_APP,
            COREAPPS_LATEST_OBS_FOR_CONCEPT_LIST_APP, COREAPPS_OBS_ACROSS_ENCOUNTERS_APP, COREAPPS_OBS_GRAPH_APP,
            COREAPPS_RELATIONSHIPS_APP, COREAPPS_MOST_RECENT_VITALS_APP);

    public static final List<String> CFL_ADDITIONAL_MODIFICATION_APP_IDS = Arrays.asList(
            CFL_PATIENT_DASHBOARD_IMPROVEMENTS_APP, CFL_RELATIONSHIPS_APP, CFL_LATESTOBSFORCONCEPTLIS_APP);

    public static final List<String> EXTENSION_IDS = Arrays.asList(APPOINTMENTSCHEDULINGUI_TAB_EXT,
            PATIENT_DASHBOARD_APPOINTMENTS_EXT, STICKY_NOTE_EXT, COREAPPS_CREATE_VISIT_EXT,
            COREAPPS_CREATE_RETROSPECTIVE_VISIT_EXT, COREAPPS_MERGE_VISITS_EXT, ALLERGYUI_PATIENT_DASHBOARD_EXT,
            PATIENTFLAGS_EXT, ATTACHMENTS_EXT, CHARTSEARCH_EXT, LOCATIONBASEDACCESS_EXT);

    public static final String REGISTRATION_APP_EDIT_PATIENT_DASHBOARD_EXT = "org.openmrs.module.registrationapp." +
            "editPatientDemographics";

    private AppFrameworkConstants() {
    }
}
