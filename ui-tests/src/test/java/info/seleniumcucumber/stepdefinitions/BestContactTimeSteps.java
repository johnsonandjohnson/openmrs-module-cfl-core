package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Then;
import org.openqa.selenium.By;

import java.util.Calendar;

import static util.StepUtils.scrollToAndClick;
import static util.StepUtils.waitAndClickOn;

public class BestContactTimeSteps {

    @Then("^I pick best contact time for (\\d+) minutes in the future$")
    public void iPickBestContactTimeForMinutesInTheFuture(int minutesToAdd) throws InterruptedException {
        Calendar time = Calendar.getInstance();
        time.add(Calendar.MINUTE, minutesToAdd);
        String hour = String.format("%02d", time.get(Calendar.HOUR_OF_DAY));
        String minute = String.format("%02d", time.get(Calendar.MINUTE));
        waitAndClickOn(By.xpath("(//input[contains(@placeholder, 'Select time')])[1]"), 1000);
        scrollToAndClick(getListElementByText(hour, 1));
        if (Integer.valueOf(minute) > 23) {
            scrollToAndClick(getListElementByText(minute, 1));
        } else {
            scrollToAndClick(getListElementByText(minute, 2));
        }
    }

    private By getListElementByText(String text, int index) {
        return By.xpath("(//li[contains(text(), '" + text + "')])[" + index + "]");
    }
}
