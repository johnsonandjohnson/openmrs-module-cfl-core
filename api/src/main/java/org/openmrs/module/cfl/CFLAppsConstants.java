package org.openmrs.module.cfl;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * The CFLApps Class collects all IDs of CFL-specific apps.
 */
public final class CFLAppsConstants {
    public static final String CFL_DISABLED_VISIT_ACTIONS = "cfl.patientDashboard.disableVisitActions";
    public static final String CFL_ALLERGIES_ACTION = "cfl.allergies.action";
    public static final String CFL_CONDITIONS_ACTION = "cfl.conditions.action";
    public static final String CFL_INIT_CALL = "cfl.init.call";
    public static final String CFL_PERSONDASHBOARD_ACTIONS = "cfl.personDashboard.actions";

    public static final String CFL_PERSONHEADER = "cfl.personHeader";

    public static final String CFL_PATIENTFLAGS_WIDGET = "cfl.patientflags.widget";
    public static final String CFL_ALLERGIES_WIDGET = "cfl.allergies.widget";
    public static final String CFL_CONDITIONS_WIDGET = "cfl.conditions.widget";
    public static final String CFL_RELATIONSHIPS_WIDGET = "cfl.relationships.widget";
    public static final String CFL_LATESTOBSFORCONCEPTLIS_WIDGET = "cfl.latestobsforconceptlis.widget";
    public static final String CFL_PATIENTPROGRAM_WIDGET = "cfl.patientProgram.widget";
    public static final String CFL_BESTCONTACT_WIDGET = "cfl.bestContact.widget";

    public static final Set<String> ALL_NAMES = new HashSet<>(
            asList(CFL_DISABLED_VISIT_ACTIONS, CFL_PATIENTFLAGS_WIDGET, CFL_ALLERGIES_ACTION, CFL_ALLERGIES_WIDGET,
                    CFL_CONDITIONS_ACTION, CFL_CONDITIONS_WIDGET, CFL_INIT_CALL, CFL_RELATIONSHIPS_WIDGET,
                    CFL_LATESTOBSFORCONCEPTLIS_WIDGET, CFL_PATIENTPROGRAM_WIDGET, CFL_PERSONDASHBOARD_ACTIONS,
                    CFL_BESTCONTACT_WIDGET, CFL_PERSONHEADER));

    private CFLAppsConstants() {

    }

}
