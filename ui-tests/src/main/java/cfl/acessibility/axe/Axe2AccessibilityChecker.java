package cfl.acessibility.axe;

import com.deque.axe.AXE;
import cfl.acessibility.AccessibilityChecker;
import cfl.acessibility.AccessibilityResults;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class Axe2AccessibilityChecker implements AccessibilityChecker {

    private static final String AXE_URL = "https://cdnjs.cloudflare.com/ajax/libs/axe-core/2.6.1/axe.js";

    @Override
    public AccessibilityResults performCheck(WebDriver webDriver) {
        try {
            AXE.inject(webDriver, new URL(AXE_URL));

            AXE.Builder builder = new AXE.Builder(webDriver, new URL(AXE_URL));
            JSONObject results = builder.analyze();

            return new AxeResults(results);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Axe url malformed", e);
        }
    }
}
