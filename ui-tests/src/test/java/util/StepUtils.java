package util;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static env.DriverUtil.getDefaultDriver;
import static env.DriverUtil.getJsExecutor;
import static env.DriverUtil.getWait;

public class StepUtils {

    public static void scrollToAndClick(By by) throws InterruptedException {
        Thread.sleep(1000);
        getJsExecutor().executeScript("arguments[0].scrollIntoView()", getDefaultDriver().findElement(by));
        getJsExecutor().executeScript("arguments[0].click()", getDefaultDriver().findElement(by));
    }

    public static void sendKeys(By to, CharSequence keys) {
        getDefaultDriver().findElement(to).sendKeys(keys);
    }

    public static void clickOn(By by) {
        getDefaultDriver().findElement(by).click();
    }

    public static void waitAndClickOn(By by, int additionalWait) throws InterruptedException {
        Thread.sleep(additionalWait);
        waitAndClickOn(by);
    }

    public static void waitAndClickOn(By by) throws InterruptedException {
        getWait().until(ExpectedConditions.elementToBeClickable(getDefaultDriver().findElement(by)));
        clickOn(by);
    }
}
