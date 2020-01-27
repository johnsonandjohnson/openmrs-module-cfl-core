package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Then;
import env.DriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NavigationSteps {

    @Then("^I navigate to patient \"([^\"]*)\" dashboard$")
    public void iNavigateToPatientDashboard(String patient) throws Throwable {
        WebDriver driver = DriverUtil.getDefaultDriver();
        driver.findElement(By.xpath("//a[contains(@href,'findpatient')]")).click();
        driver.findElement(By.id("patient-search")).sendKeys(patient);
        driver.findElement(By.xpath("//td[contains(text(),'" + patient + "')]")).click();
    }
}
