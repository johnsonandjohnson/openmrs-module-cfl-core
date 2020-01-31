package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Given;
import org.openqa.selenium.By;
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

    private void fillLoginForm() {
        clickOn(By.id("Inpatient Ward"));
        sendKeys(By.id("username"), ContextService.getUsername());
        sendKeys(By.id("password"), ContextService.getPassword());
        clickOn(By.id("loginButton"));
    }
}
