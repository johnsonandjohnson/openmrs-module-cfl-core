package org.openmrs.module.cfl;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * The CFLApps Class collects all IDs of CFL-specific apps.
 */
public final class CFLAppsConstants {
    public static final String CFL_REGISTERCAREGIVER = "cfl.registerCaregiver";
    public static final String CFL_FINDPERSON = "cfl.findPerson";
    public static final String CFL_PATIENTDASHBOARD_IMPROVEMENTS = "cfl.patientDashboard.improvements";
    public static final String CFL_PATIENTFLAGS = "cfl.patientflags";
    public static final String CFL_ALLERGIES = "cfl.allergies";
    public static final String CFL_CONDITIONS = "cfl.conditions";
    public static final String CFL_INIT_CALL = "cfl.init.call";
    public static final String CFL_PATIENTDASHBOARD_RELATIONSHIPS = "cfl.patientDashboard.relationships";
    public static final String CFL_LATESTOBSFORCONCEPTLIS = "cfl.latestobsforconceptlis";
    public static final String CFL_PATIENTPROGRAM_LIST = "cfl.patientProgram.list";
    public static final String CFL_PERSONDASHBOARD = "cfl.personDashboard";
    public static final String CFL_PERSONDASHBOARD_BESTCONTACT = "cfl.personDashboard.bestContact";
    public static final String CFL_PERSONDASHBOARD_RELATIONSHIPS = "cfl.personDashboard.relationships";
    public static final String CFL_PERSONHEADER = "cfl.personHeader";
    public static final String CFL_REGISTERPATIENT = "cfl.registerPatient";

    public static final Set<String> ALL_NAMES = new HashSet<>(
            asList(CFL_REGISTERCAREGIVER, CFL_FINDPERSON, CFL_PATIENTDASHBOARD_IMPROVEMENTS, CFL_PATIENTFLAGS, CFL_ALLERGIES,
                    CFL_CONDITIONS, CFL_INIT_CALL, CFL_PATIENTDASHBOARD_RELATIONSHIPS, CFL_LATESTOBSFORCONCEPTLIS,
                    CFL_PATIENTPROGRAM_LIST, CFL_PERSONDASHBOARD, CFL_PERSONDASHBOARD_BESTCONTACT,
                    CFL_PERSONDASHBOARD_RELATIONSHIPS, CFL_PERSONHEADER, CFL_REGISTERPATIENT));

    private CFLAppsConstants() {

    }

}
