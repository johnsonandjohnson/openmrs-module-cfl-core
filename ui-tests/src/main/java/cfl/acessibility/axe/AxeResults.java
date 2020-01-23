package cfl.acessibility.axe;

import com.deque.axe.AXE;
import cfl.acessibility.AccessibilityResults;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintStream;

public class AxeResults implements AccessibilityResults {

    private static final String PASSES = "passes";
    private static final String INCOMPLETE = "incomplete";
    private static final String INAPPLICABLE = "inapplicable";
    private static final String VIOLATIONS = "violations";

    private final JSONArray passes;
    private final JSONArray incomplete;
    private final JSONArray inapplicable;
    private final JSONArray violations;

    AxeResults(JSONObject jsonObj) {
        passes = jsonObj.optJSONArray(PASSES);
        incomplete = jsonObj.optJSONArray(INCOMPLETE);
        inapplicable = jsonObj.optJSONArray(INAPPLICABLE);
        violations = jsonObj.optJSONArray(VIOLATIONS);
    }

    public int countPasses() {
        return passes == null ? 0 : passes.length();
    }

    public int countIncomplete() {
        return incomplete == null ? 0 : incomplete.length();
    }

    public int countInapplicable() {
        return inapplicable == null ? 0 : inapplicable.length();
    }

    @Override
    public int countViolations() {
        return violations == null ? 0 : violations.length();
    }

    @Override
    public boolean hasViolations() {
        return countViolations() > 0;
    }

    @Override
    public void print(PrintStream out) {
        out.println(String.format("Found %d accessibility violations. %d rules passed. " +
                "%d rules incompletely tested, %d rules inapplicable.", countViolations(), countPasses(),
                countIncomplete(), countInapplicable()));

        if (hasViolations()) {
            out.println(AXE.report(violations));
        }
    }
}
