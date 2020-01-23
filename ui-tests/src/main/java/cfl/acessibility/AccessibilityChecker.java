package cfl.acessibility;

import org.openqa.selenium.WebDriver;

public interface AccessibilityChecker {

    AccessibilityResults performCheck(WebDriver webDriver);
}
