package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static env.DriverUtil.getDefaultDriver;

public class CalendarSteps {

    @Then("^today's calendar tile contains \"([^\"]*)\"$")
    public void todaySCalendarTileContains(String eventName) {
        //TODO: verify if the event is properly visible
    }
}
