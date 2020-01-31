package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.beans.IntrospectionException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static env.DriverUtil.getDefaultDriver;
import static env.DriverUtil.getJsExecutor;
import static env.DriverUtil.getWait;
import static util.StepUtils.scrollToAndClick;
import static util.StepUtils.waitAndClickOn;

public class BestContactTimeSteps {

    @Then("^I pick best contact time for (\\d+) minutes in the future$")
    public void iPickBestContactTimeForMinutesInTheFuture(int minutesToAdd) throws InterruptedException {
        Date time = Date.from(new Date().toInstant().plusSeconds(60 * minutesToAdd));
        String hour = String.format("%02d", time.getHours());
        String minute = String.format("%02d", time.getMinutes());
        waitAndClickOn(By.xpath("(//input[contains(@placeholder, 'Select time')])[1]"), 1000);
        scrollToAndClick(By.xpath("(//li[contains(text(), '" + hour + "')])[1]"));
        if (Integer.valueOf(minute) > 23) {
            scrollToAndClick(By.xpath("(//li[contains(text(), '" + minute + "')])[1]"));
        } else {
            scrollToAndClick(By.xpath("(//li[contains(text(), '" + minute + "')])[2]"));
        }
    }
}
