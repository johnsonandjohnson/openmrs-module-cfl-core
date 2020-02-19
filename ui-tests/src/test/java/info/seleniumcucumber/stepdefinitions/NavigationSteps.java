package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Then;
import org.openqa.selenium.By;
import service.ContextService;

import static env.DriverUtil.getDefaultDriver;

public class NavigationSteps {

    @Then("^I navigate to patient \"([^\"]*)\" dashboard$")
    public void iNavigateToPatientDashboard(String patient) {
        getDefaultDriver().get(ContextService.getServerUrl() + "/referenceapplication/home.page");
        getDefaultDriver().findElement(By.xpath("//a[contains(@href,'findpatient')]")).click();
        getDefaultDriver().findElement(By.id("patient-search")).sendKeys(patient);
        getDefaultDriver().findElement(By.xpath("//td[contains(text(),'" + patient + "')]")).click();
    }

    @Then("^I navigate to patient \"([^\"]*)\" messages$")
    public void iNavigateToPatientMessages(String patient) {
        navigateToPatientDashboardElement(patient, "messagesapp.basicMessages.patientDashboardLink");
    }

    @Then("^I navigate to patient \"([^\"]*)\" visits$")
    public void iNavigateToPatientVisits(String patient) {
        navigateToPatientDashboardElement(patient, "visitsapp.basicVisits.patientDashboardLink");
    }

    private void navigateToPatientDashboardElement(String patient, String elementId) {
        iNavigateToPatientDashboard(patient);
        getDefaultDriver().findElement(By.id(elementId)).click();
    }
}
