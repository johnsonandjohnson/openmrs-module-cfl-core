package cfl.test;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static env.DriverUtil.getDefaultDriver;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "pretty", "html:target/cucumberHtmlReport",
                "html:target/cucumberHtmlReport",     //  for html result
                "pretty:target/cucumber-json-report.json"   // for json result
        },
        features = "classpath:features",
        glue = {
                "info.seleniumcucumber.stepdefinitions",   // predefined step definitions package
                "cfl.test.steps", // user step definitions package
                "env" // hooks definitions package
        }
)

public class RunCukeTest {

    private static final int TIMEOUT_SECONDS = 60;

    @BeforeClass
    public static void setUp() {
        getDefaultDriver().manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        getDefaultDriver().manage().timeouts().pageLoadTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void close() {
    }
}
