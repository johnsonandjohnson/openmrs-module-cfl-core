package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Given;
import env.DriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import service.ContextService;

public class LoginSteps {

    @Given("^I log in$")
    public void iLogIn() {
        WebDriver driver = DriverUtil.getDriver();
        driver.get(ContextService.getServerUrl() + "/referenceapplication/login.page?showSessionLocations=true&");
        fillLoginForm(driver);
    }

    private void fillLoginForm(WebDriver driver) {
        driver.findElement(By.id("Inpatient Ward")).click();
        driver.findElement(By.id("username")).sendKeys(ContextService.getUsername());
        driver.findElement(By.id("password")).sendKeys(ContextService.getPassword());
        driver.findElement(By.id("loginButton")).click();
    }
}
