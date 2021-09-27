package org.openmrs.module.cfl.domain.criteria.messages;

import com.google.gson.Gson;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfl.api.contract.Vaccination;
import org.openmrs.module.cfl.api.contract.VisitInformation;
import org.openmrs.module.messages.domain.criteria.InMemoryCondition;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The ReceivedDosagesCondition Class.
 * <p>
 * The Condition which filters Patients by number of vaccination dosages they received. The vaccination program is defined
 * by Global Property with name {@link CFLConstants#VACCINATION_PROGRAM_KEY}. The number of vaccination dosages is
 * determined by occurred Visits of Visit Types defined by vaccination program. The solution relies on that the Visits
 * will occur one after the other in time (the dosage Visits are not skipped, there are no Visits with multiple dosages)
 * <br />
 * E.g.: if the Visit having VisitType defined in vaccination program with dosage number 2 occurred for given patient,
 * then the Patient had at least two dosage received.
 * </p>
 * <p>
 * Implementation details: <br />
 * The Hibernate version which is being used by OpenMRS at the time of this class creation, does not support making joins
 * using Criteria on related entities without mapped association (if the Patient has no mapped collection of visits, it's
 * impossible to make a join the visits). This implementation works around it by reading IDs of all possible patients,
 * based on occurred visits, and then filter the list of Patients in 'post-db-read' manner.
 * <br />
 * Why it's {@link InMemoryCondition} and not {@link org.openmrs.module.messages.domain.criteria.HibernateCondition} with
 * criteria condition on patient IDs? A: There is a limit to the maximum size of the List which can be passed as parameter to
 * the Criteria query (and SQL query in general).
 * </p>
 */
public class ReceivedDosagesCondition implements InMemoryCondition {
    private static final int FIRST = 0;

    /**
     * The SQL to read Patient IDs which received the expected number of dosages.
     * <p>
     * This is message format, with single parameter where it's expected to put a String with list of Visit Type names.
     * The String must be valid part of the SQL.
     * </p>
     */
    private static final String READ_PATIENT_IDS_FOR_DOSAGE_VISITS_SQL = "SELECT p.patient_id \n" + //
            "FROM patient p\n" + //
            "WHERE (\n" + //
            "    SELECT included_vt.name\n" + //
            "    FROM visit included_v\n" + //
            "    INNER JOIN visit_type included_vt\n" + //
            "        ON included_vt.visit_type_id = included_v.visit_type_id\n" + //
            "    INNER JOIN visit_attribute included_va\n" + //
            "        ON included_va.visit_id = included_v.visit_id and included_va.voided = 0\n" + //
            "    INNER JOIN visit_attribute_type included_vat\n" + //
            "        ON included_vat.visit_attribute_type_id = included_va.attribute_type_id\n" + //
            "    WHERE included_v.patient_id = p.patient_id\n" + //
            "        and included_v.voided = 0\n" + //
            "        and included_vat.name = ''Visit Status''\n" + //
            "        and included_va.value_reference = ''OCCURRED''\n" + //
            "    ORDER BY included_v.date_started DESC\n" + //
            "    LIMIT 1\n" + //
            ") IN ({0})";

    private static final Map<String, VisitTypeSelector> SELECTORS;

    static {
        final Map<String, VisitTypeSelector> selectorsTmp = new HashMap<String, VisitTypeSelector>();
        selectorsTmp.put("=", new EQFunction());
        selectorsTmp.put(">", new GTFunction());
        selectorsTmp.put("<", new LTFunction());
        selectorsTmp.put(">=", new GEFunction());
        selectorsTmp.put("<=", new LEFunction());
        SELECTORS = Collections.unmodifiableMap(selectorsTmp);
    }

    private Set<Integer> patientIds;

    public ReceivedDosagesCondition(final String operator, final Integer dosageCount) {
        final Set<String> visitTypesForCondition = getVisitTypesForCondition(operator, dosageCount);
        final List<List<Object>> includedPatientsSQLResult = executeConditionDataSQL(visitTypesForCondition);

        this.patientIds = new HashSet<Integer>(toPatientIds(includedPatientsSQLResult));
    }

    @Override
    public Boolean applyCondition(OpenmrsObject openmrsObject) {
        return patientIds.contains(openmrsObject.getId());
    }

    private Set<String> getVisitTypesForCondition(final String operator, final Integer dosageCount) {
        final Set<String> visitTypesForCondition = new HashSet<String>();

        final Map<Integer, Set<String>> visitTypesByDosageCount = getVisitTypesByDosageCount();
        final VisitTypeSelector operatorFunction = SELECTORS.get(operator.trim());

        for (final Map.Entry<Integer, Set<String>> visitTypesByDosageCountEntry : visitTypesByDosageCount.entrySet()) {
            if (operatorFunction.include(visitTypesByDosageCountEntry.getKey(), dosageCount)) {
                visitTypesForCondition.addAll(visitTypesByDosageCountEntry.getValue());
            }
        }

        return visitTypesForCondition;
    }

    private List<List<Object>> executeConditionDataSQL(final Set<String> visitTypesForCondition) {
        if (visitTypesForCondition.isEmpty()) {
            return Collections.emptyList();
        }

        final String conditionDataSQL =
                MessageFormat.format(READ_PATIENT_IDS_FOR_DOSAGE_VISITS_SQL, convertToSQLList(visitTypesForCondition));

        return Context.getAdministrationService().executeSQL(conditionDataSQL, true);
    }

    private String convertToSQLList(final Set<String> visitTypes) {
        final StringBuilder builder = new StringBuilder();
        for (String visitType : visitTypes) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append('\'').append(visitType).append('\'');
        }
        return builder.toString();
    }

    private Map<Integer, Set<String>> getVisitTypesByDosageCount() {
        final Gson gson = new Gson();
        final String vaccinationsJson =
                Context.getAdministrationService().getGlobalProperty(CFLConstants.VACCINATION_PROGRAM_KEY);
        final Vaccination[] vaccinations = gson.fromJson(vaccinationsJson, Vaccination[].class);

        final Map<Integer, Set<String>> result = new HashMap<Integer, Set<String>>();

        if (vaccinations != null) {
            for (final Vaccination vaccination : vaccinations) {
                for (final VisitInformation visitInformation : vaccination.getVisits()) {
                    if (result.get(visitInformation.getDoseNumber()) == null) {
                        result.put(visitInformation.getDoseNumber(), new HashSet<String>());
                    }

                    result.get(visitInformation.getDoseNumber()).add(visitInformation.getNameOfDose());
                }
            }
        }

        return result;
    }

    private List<Integer> toPatientIds(final List<List<Object>> patientIDsSQLResult) {
        final List<Integer> result = new ArrayList<Integer>();

        for (List<Object> resultRow : patientIDsSQLResult) {
            result.add(((Number) resultRow.get(FIRST)).intValue());
        }

        return result;
    }

    private interface VisitTypeSelector {
        boolean include(int leftOperand, int rightOperand);
    }

    private static class EQFunction implements VisitTypeSelector {
        @Override
        public boolean include(int leftOperand, int rightOperand) {
            return leftOperand == rightOperand;
        }
    }

    private static class GTFunction implements VisitTypeSelector {
        @Override
        public boolean include(int leftOperand, int rightOperand) {
            return leftOperand > rightOperand;
        }
    }

    private static class LTFunction implements VisitTypeSelector {
        @Override
        public boolean include(int leftOperand, int rightOperand) {
            return leftOperand < rightOperand;
        }
    }

    private static class GEFunction implements VisitTypeSelector {
        @Override
        public boolean include(int leftOperand, int rightOperand) {
            return leftOperand >= rightOperand;
        }
    }

    private static class LEFunction implements VisitTypeSelector {
        @Override
        public boolean include(int leftOperand, int rightOperand) {
            return leftOperand <= rightOperand;
        }
    }
}
