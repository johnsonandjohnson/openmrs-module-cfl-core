package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static env.DriverUtil.getDefaultDriver;
import static env.DriverUtil.getJsExecutor;
import static env.DriverUtil.getWait;

public class ButtonSteps {

    @Then("^I wait and click on element having xpath \"([^\"]*)\"$")
    public void iWaitAndClickOnElementHavingXpath(String xpath) {
        getWait().until(ExpectedConditions.elementToBeClickable(getDefaultDriver().findElement(By.xpath(xpath))));
        getJsExecutor().executeScript("arguments[0].click()", getDefaultDriver().findElement(By.xpath(xpath)));
    }
}
