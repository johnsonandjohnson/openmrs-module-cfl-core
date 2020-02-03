package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import service.ContextService;

import static env.DriverUtil.getDefaultDriver;
import static util.StepUtils.clickOn;
import static util.StepUtils.sendKeys;

public class LoginSteps {

    @Given("^I log in$")
    public void iLogIn() {
        getDefaultDriver().get(ContextService.getServerUrl() + "/referenceapplication/login.page?showSessionLocations=true&");
        fillLoginForm();
    }

    @Then("^I log out$")
    public void iLogOut() {
        logOut(getDefaultDriver());
    }

    private void fillLoginForm() {
        clickOn(By.id("Inpatient Ward"));
        sendKeys(By.id("username"), ContextService.getUsername());
        sendKeys(By.id("password"), ContextService.getPassword());
        clickOn(By.id("loginButton"));
    }

    private void logOut(WebDriver driver) {
        driver.findElement(By.xpath("(//li[contains(@class, 'logout')])//a")).click();
    }
}
