package env;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.openqa.selenium.By;
import service.ContextService;
import service.RegistrationService;

import static env.DriverUtil.getDefaultDriver;
import static util.StepUtils.clickOn;
import static util.StepUtils.sendKeys;

public class Hooks {

    @Before("@WithPatients")
    public void registerPatient() {
        RegistrationService.initializePatients();
    }

    @After("@WithPatients")
    public void unregisterPatient() {
        RegistrationService.unregisterPatients();
    }

    @Before()
    public void logIn() {
        getDefaultDriver().get(ContextService.getServerUrl() + "/cfldistribution/login.page?showSessionLocations=true&");
        fillLoginForm();
    }

    @After()
    public void logOut() {
        getDefaultDriver().findElement(By.xpath("(//li[contains(@class, 'logout')])//a")).click();
    }

    private void fillLoginForm() {
        clickOn(By.id("Inpatient Ward"));
        sendKeys(By.id("username"), ContextService.getUsername());
        sendKeys(By.id("password"), ContextService.getPassword());
        clickOn(By.id("loginButton"));
    }
}
