package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Then;
import org.openqa.selenium.By;

import java.util.Calendar;
import java.util.Locale;

import static util.StepUtils.clickOn;
import static env.DriverUtil.getDefaultDriver;

public class ManageMessagesSteps {

    @Then("^I ensure today is checked as a weekday of delivering messages$")
    public void iEnsureTodayIsCheckedAsAWeekdayOfDeliveringMessages() {
        String day = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        By checkbox = By.xpath("//div[contains(@id,'Adherence report daily')]" +
                "//input[@type='checkbox' and @name='" + day + "']");
        if (!getDefaultDriver().findElement(checkbox).isSelected()) {
            clickOn(checkbox);
        }
    }
}
