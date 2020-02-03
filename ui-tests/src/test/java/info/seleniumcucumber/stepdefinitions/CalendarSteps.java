package info.seleniumcucumber.stepdefinitions;

import cucumber.api.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static env.DriverUtil.getDefaultDriver;
import static env.DriverUtil.getWait;

public class CalendarSteps {

    @Then("^today's calendar tile contains \"([^\"]*)\"$")
    public void todaySCalendarTileContains(String eventName) throws InterruptedException {
        Thread.sleep(3000);
        getWait().until(ExpectedConditions.elementToBeClickable(getDefaultDriver().findElement(By.xpath("//button[contains(text(),'Manage messages')]"))));
        getDefaultDriver().findElement(By.xpath(getTodaySEventsCellXpath() + "/a/div/span[contains(text(), '" + eventName + "')]"));
    }

    private String getTodaySEventsCellXpath() {
        String today = "td[contains(@class, 'fc-today')]";
        //there are multiple tables nested in the calendar
        return "//table/thead/tr/" + today + //find today's table header
                "/parent::tr/parent::thead/parent::table" + //get the desired table from the header ancestors
                "/tbody/tr/td[" + //get desired cell by index counted below
                "count(//thead/tr/" + today + "/preceding-sibling::td)+1" + //count how many columns are before today
                "]";
    }
}
